package de.hoepmat.services;

import de.hoepmat.common.Constants;
import de.hoepmat.util.CommandShell;
import de.hoepmat.web.StateHolder;
import de.hoepmat.web.model.State;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.hoepmat.common.Constants.*;

/**
 * Created by hoepmat on 11/16/15.
 * <p/>
 * <p/>
 * git svn clone --username hans:myPassword --trunk=/der/pfad/zu/tunk -rSVN_REVISION_NUMBER:HEAD https://URL_ZUM_REPOSITORY gitrepo_sync
 * <p/>
 * -------------------------------------------------------------------------------------------------------
 * ERROR:
 * InvalidConfigurationException: No value for key remote.origin.url found in configuration
 * REASON:
 * Synchronization Repository has no origin remote
 * SOLUTION:
 * git remote add origin [URL/PATH to remote (i.e. https://github.com/user/repo.git)] *
 * -------------------------------------------------------------------------------------------------------
 * ERROR:
 * occured only when software is started fom IDE
 * java.lang.RuntimeException: [Can't locate Git/SVN.pm in @INC (@INC
 * REASON:
 * unknown by now
 * SOLUTION:
 * unknown by now
 * -------------------------------------------------------------------------------------------------------
 */
@Component
public class CommitChecker {
    public static final String SOMETHING_WENT_WRONG_ON_CHECKOUT_THE_SVN_REMOTE =
            "Something went wrong on checkout the svn remote.";
    public static final String UNEXPECTED_ERROR_OCCURED =
            "An unexpected error occured. Please check the log!";
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(CommitChecker.class.getName());
    @Value("${path.to.centralRepository}")
    private String centralRepositoryPath;

    @Value("${path.to.syncRepository}")
    private String syncRepositoryPath;

    @Value("${branch.name.master}")
    private String master;

    @Value("${branch.name.svnSyncBranch}")
    private String svnSyncBranch;

    @Value("${path.to.git.executable}")
    private String pathToGitExecutable;

    @Value("${conflict.solving.strategy}")
    private String solveConflictStrategy;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private LockFileService lockFileService;

    @Autowired
    private MailService mailService;

    @Autowired
    private StatusLoggerService loggerService;

    @Autowired
    private SyncCommitMessageService commitMessageService;

    @Autowired
    private StateHolder stateHolder;

    @Autowired
    private CommandShell commandShell;

    private ArrayList<String> conflictingFiles = new ArrayList<String>();

    @Scheduled(fixedDelayString = "${synchronization.scheduled.delay}")
    public void syncWithSvn() throws IOException {
        Repository syncRepo = null;
        try {
            if (stateHolder.getState().isSuspend()) {
                LOGGER.info("... I am sleeping ;-) - reactivate me with http://hostname:" + serverPort + "/start");
                return;
            }
            long startTime = System.currentTimeMillis();
            conflictingFiles.clear();
            LOGGER.info(FAT_LINE);
            LOGGER.info("### Syncronization is starting");
            LOGGER.info(FAT_LINE);
            stateHolder.getState().setMessage("OKAY - starting");

            lockFileService.createLock();

            syncRepo = new FileRepositoryBuilder().setGitDir(new File(syncRepositoryPath)).build();
            syncRepo.getConfig().load();
            Git git = new Git(syncRepo);
            loggerService.loggStatus();

            // first we do a fetch
            git.fetch().call();
            loggerService.loggStatus();

            tryToPullIntoMergeSourceBranch(git, master);

            final Ref tipOfBranchMaster = getTipOfBranch(git, master);
            final Ref tipOfBranchSvnSync = getTipOfBranch(git, svnSyncBranch);

            LinkedList<RevCommit> commitDiff =
                    getCommitDifference(git, tipOfBranchSvnSync, tipOfBranchMaster);

            String syncCommitMessage = commitMessageService.getSyncCommitMessage(commitDiff, tipOfBranchSvnSync);

            final CheckoutCommand checkoutCommand = git.checkout().setName(svnSyncBranch);
            checkoutCommand.call();
            final CheckoutResult checkoutResult = checkoutCommand.getResult();
            if (!checkoutResult.getStatus().equals(CheckoutResult.Status.OK)) {
                LOGGER.severe(SOMETHING_WENT_WRONG_ON_CHECKOUT_THE_SVN_REMOTE);
                mailService.sendErrorMessage(SOMETHING_WENT_WRONG_ON_CHECKOUT_THE_SVN_REMOTE);
            }

            // identify all contributors and collect the emails
            final HashSet<String> committerEmails = getCommitterEmails(commitDiff);

            // start the synchronization
            final boolean syncWas2Way = start2WaySync(git, tipOfBranchMaster, syncCommitMessage);

            // logg out the state after synchronization is done
            loggerService.loggStatus();

            StringBuilder message = new StringBuilder("Commit message is: ")
                    .append(syncCommitMessage)
                    .append("\n");
            StringBuilder subject = new StringBuilder("Synchronization was successful!");

            if (conflictingFiles.size() > 0) {
                subject.append("(With conflicting files!)");
                message.append("Conflicting files:\n");
            }
            for (String conflictingFile : conflictingFiles) {
                message.append(conflictingFile).append("\n");
            }

            final State state = stateHolder.getState();
            if (syncWas2Way) {
                mailService.sendMail(
                        null,
                        committerEmails,
                        null,
                        subject.toString(),
                        message.toString(),
                        null);

                state.setMessage("OKAY - successfull two way sync. Finished at: " + new Date());
                commitMessageService.resetCommitMessage();
            } else {
                state.setMessage("OKAY - successfull one way sync. Finished at: " + new Date());
            }
            state.setTimeSpend(System.currentTimeMillis() - startTime);

            lockFileService.releaseLock();
            LOGGER.info("Sync run finshed");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, UNEXPECTED_ERROR_OCCURED, e);
            mailService.sendErrorMessage(UNEXPECTED_ERROR_OCCURED + " " + e.getMessage());
            stateHolder.getState().setMessage(UNEXPECTED_ERROR_OCCURED + " " + e.getMessage());
        }
        finally {
            if (syncRepo != null) {
                syncRepo.close();
            }
        }
    }

    private boolean start2WaySync(Git git, Ref masterBranchHead, String mergeMessage) throws IOException, GitAPIException {
        boolean syncDone = false;

        // fetch changes from SVN
        LOGGER.info("try to fetch changes from svn");
        ArrayList<String> result = commandShell.runCommand(Constants.COMMAND_GIT_SVN_FETCH);
        if (result != null) {
            for (String line : result) {
                LOGGER.info("   " + line);
            }
        }
        LOGGER.info(SIMPLE_LINE);

        // checkout svn remote branch
        try {
            git.checkout().setName(svnSyncBranch).call();
        } catch (GitAPIException e) {
            final String msg = "Error on checkout branch [" + svnSyncBranch + "]";
            throw new RuntimeException(msg, e);
        }

//        LOGGER.info("try to create the TMP branch");
//        final Ref sync_tmp = git.checkout().setCreateBranch(true).setName("SYNC_TMP").call();
//        LOGGER.info("Branch angelegt: " + sync_tmp.getName());

        // merge git src into svn remote
        boolean okay = doMergeNoFF(git, masterBranchHead, mergeMessage);

        // svn dcommit
        if (okay) {
            LOGGER.info("try a SVN dcommit");
            ArrayList<String> svnCommitResult = commandShell.runCommand(Constants.COMMAND_GIT_SVN_DCOMMIT);
            syncDone = true;
        }

        LOGGER.info("Try to merge svn remote back to master");
        git.checkout().setName(master).call();

        final Ref tipOfBranch = getTipOfBranch(git, svnSyncBranch);
        okay = doMergeFF(git, tipOfBranch, mergeMessage);
        if (okay) {
            LOGGER.info("Try to push to central repository");
            git.push().setRemote("origin").call();
//            git.branchDelete().setBranchNames("SYNC_TMP").call();
        } else {
            LOGGER.info("Nothing to merge so nothing to push...");
        }

        return syncDone;
    }

    private boolean doMergeNoFF(Git git, Ref refToMerge, String mergeMessage) throws IOException {
        return doMerge(git, refToMerge, mergeMessage, true, false);
    }

    private boolean doMergeFF(Git git, Ref refToMerge, String mergeMessage) throws IOException {
        return doMerge(git, refToMerge, mergeMessage, false, false);
    }

    private boolean doMerge(Git git, Ref refToMerge, String mergeMessage, boolean no_ff, boolean solveConflict) throws IOException {
        loggerService.loggStatus();

        if (no_ff && (mergeMessage == null || mergeMessage.isEmpty())) {
            LOGGER.info("Commit message is empty for a NO_FF merge. That means, there is nothing to do...");
            return false;
        }

        LOGGER.info("Start a merge [" + refToMerge.getObjectId() + " = " + refToMerge.getName() + "]");
        final MergeCommand mergeCommand = git.merge();
        mergeCommand.include(refToMerge);
        if (no_ff) {
            mergeCommand.setMessage(mergeMessage);
            mergeCommand.setFastForward(MergeCommand.FastForwardMode.NO_FF);
        } else {
            mergeCommand.setFastForward(MergeCommand.FastForwardMode.FF);
        }

        if (solveConflict) {
            if (solveConflictStrategy != null) {
                final MergeStrategy mergeStrategy = MergeStrategy.get(solveConflictStrategy);

                // default MergeStrategy is THEIRS
                mergeCommand.setStrategy(mergeStrategy == null ? MergeStrategy.THEIRS : mergeStrategy);
            }
        }

        try {
            final MergeResult mergeResult = mergeCommand.call();
            final Map<String, int[][]> conflicts = mergeResult.getConflicts();

            if (conflicts != null) {
                // it is the second run in that recursion. Conflicts still present so time to say good bye ;-)
                // and give the admins some hints
                if (solveConflict) {
                    StringBuilder errorLines = new StringBuilder();
                    errorLines.append("There are conflicts:\n");
                    for (String key : conflicts.keySet()) {
                        final String msg = key + " - " + Arrays.deepToString(conflicts.get(key));
                        errorLines.append(msg).append("\n");
                        LOGGER.severe("conflicts in file: " + msg);
                    }
                    throw new RuntimeException(errorLines.toString());
                } else {

                    for (String key : conflicts.keySet()) {
                        final String msg = key + " - " + Arrays.deepToString(conflicts.get(key));
                        conflictingFiles.add(msg);
                    }

                    // clean all before we try to start it again
                    try {
                        git.rebase().setOperation(RebaseCommand.Operation.ABORT).call();
                    } catch (NoHeadException e) {
                    } catch (WrongRepositoryStateException e) {
                    } catch (CheckoutConflictException e) {
                    } catch (RefNotFoundException e) {
                    } catch (GitAPIException e) {
                    }

                    git.reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD").call();

                    LOGGER.info("try to solve the conflict...");
                    doMerge(git, refToMerge, mergeMessage, no_ff, true);
                    return true;
                }
            }

            LOGGER.info(DOUBLE_LINE);
            LOGGER.info("MERGE RESULT");
            LOGGER.info(mergeResult.toString());
            LOGGER.info(DOUBLE_LINE);
            return !mergeResult.getMergeStatus().equals(MergeResult.MergeStatus.ALREADY_UP_TO_DATE);
        } catch (GitAPIException e) {
            throw new RuntimeException("There is a problem with the merge.");
        }
    }

    private HashSet<String> getCommitterEmails(Iterable<RevCommit> commitDiff) {
        LOGGER.info(DOUBLE_LINE);
        LOGGER.info("try to find commiter email addresses.");
        LOGGER.info(SIMPLE_LINE);
        HashSet<String> emailAddresses = new HashSet<String>();
        for (RevCommit commit : commitDiff) {
            final String emailAddress = commit.getAuthorIdent().getEmailAddress();
            LOGGER.info(String.format("Found [%s]", emailAddress));
            emailAddresses.add(emailAddress);
        }

        return emailAddresses;
    }

    private Ref getTipOfBranch(Git git, String branchName) throws GitAPIException {
        final List<Ref> refList = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (Ref ref : refList) {
            if (ref.getName().endsWith(branchName)) {
                return ref;
            }
        }
        return null;
    }

    private LinkedList<RevCommit> getCommitDifference(Git git, Ref referenceSVN, Ref currentHead)
            throws IncorrectObjectTypeException, MissingObjectException, GitAPIException {
        final LogCommand logCommand =
                git.log().addRange(referenceSVN.getObjectId(), currentHead.getObjectId());
        final Iterable<RevCommit> commits = logCommand.call();

        LinkedList<RevCommit> result = new LinkedList<RevCommit>();
        for (RevCommit commit : commits) {
            result.add(commit);
        }

        return result;
    }

    private void tryToPullIntoMergeSourceBranch(Git git, String branchName) throws GitAPIException {
        git.checkout().setName(branchName).call();
        git.pull().setRemote("origin").setRebase(true).call();
    }
}
