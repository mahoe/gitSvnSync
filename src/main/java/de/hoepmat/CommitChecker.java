package de.hoepmat;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by hoepmat on 11/16/15.
 *
 *
 * git svn clone --username hans:myPassword --trunk=/der/pfad/zu/tunk -rSVN_REVISION_NUMBER:HEAD https://URL_ZUM_REPOSITORY gitrepo_sync
 *
 * -------------------------------------------------------------------------------------------------------
 * ERROR:
 * InvalidConfigurationException: No value for key remote.origin.url found in configuration
 * REASON:
 * Synchronization Repository has no origin remote
 * SOLUTION:
 * git remote add origin [URL/PATH to remote (i.e. https://github.com/user/repo.git)] *
 * -------------------------------------------------------------------------------------------------------
 *
 */
@Component
public class CommitChecker
{
    /** The logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(CommitChecker.class.getName());

    @Value("${path.to.centralRepository}")
    private String centralRepositoryPath;

    @Value("${path.to.syncRepository}")
    private String syncRepositoryPath;



    @Value("${branch.name.master}")
    private String master;

    @Value("${branch.name.svnSyncBranch}")
    private String svnSyncBranch;

    @Autowired
    private LockFileService lockFileService;

    public CommitChecker()
    {
        LOGGER.info("CommitChecker");
    }

    @Scheduled(fixedDelay = 1000 * 10 )
    public void listNewCommit() throws IOException
    {
        LOGGER.info("##############################################################################");
        lockFileService.createLock();

        Repository syncRepo = new FileRepositoryBuilder().setGitDir(new File(syncRepositoryPath)).build();

        Git git = new Git(syncRepo);
        final FetchCommand fetch = git.fetch();
        try
        {
            showCurrentState(git);
            loggAllBranches(git);
            loggLastCommits(git,10);

            tryToPullIntoMergeSourceBranch(git, master);

            final Ref tipOfBranchMaster = getTipOfBranch(git, master);
            final Ref tipOfBranchSvnSync = getTipOfBranch(git, svnSyncBranch);

            Iterable<RevCommit>
                    commitDiff = getCommitDifference(git, tipOfBranchSvnSync, tipOfBranchMaster);

            final Iterator<RevCommit> iterator = commitDiff.iterator();
            while (iterator.hasNext()){
                final RevCommit commit = iterator.next();
                LOGGER.info(String.format("Commit: %s", commit.getFullMessage()));
            }

            //            System.out.println(fetchResult.toString());
//            System.out.println(status.isClean());

            //            AnyObjectId startCommit = git.checkout().setName("master").call().getObjectId();
            //            AnyObjectId currentHead ;
            //            log.addRange(startCommit, currentHead);

        }
        catch (GitAPIException e) {
            System.out.println("das ging wohl nicht...");
            e.printStackTrace();
        } finally
        {
            lockFileService.releaseLock();
        }
    }

    private Ref getTipOfBranch(Git git, String branchName) throws GitAPIException
    {
        return git.checkout().setName(branchName).call();
    }

    private Iterable<RevCommit> getCommitDifference(Git git, Ref referenceSVN,
                                                  Ref currentHead)
            throws IncorrectObjectTypeException, MissingObjectException, GitAPIException
    {
        final LogCommand logCommand =
                git.log().addRange(referenceSVN.getObjectId(), currentHead.getObjectId());
        return logCommand.call();
    }

    private void tryToPullIntoMergeSourceBranch(Git git, String branchName) throws GitAPIException
    {
        git.checkout().setName(branchName).call();
        git.pull().setRebase(true).call();
    }

    private void loggAllBranches(Git git) throws GitAPIException
    {
        LOGGER.info("------------------------------------------------------------------------------");
        LOGGER.info("list of available branches:");
        final List<Ref> refList = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (Ref ref : refList)
        {
            LOGGER.info(String .format("%25s - %s",ref.getObjectId().getName(), ref.getName()));
        }
    }

    private void loggLastCommits(Git git, int numberOfCommits) throws GitAPIException
    {
        LOGGER.info("------------------------------------------------------------------------------");
        LOGGER.info(String.format("Die letzten %d commits sind:",numberOfCommits));
        final LogCommand log = git.log();
        log.setMaxCount(numberOfCommits);

        final Iterable<RevCommit> commits = log.call();
        for (RevCommit commit : commits)
        {
            LOGGER.info(String.format("%25s - %s", commit.getId().getName(), commit.getFullMessage()));
        }
    }

    private void showCurrentState(Git git) throws GitAPIException
    {
        LOGGER.info("Aktueller Status des Repositories:");
        final Status status = git.status().call();

        if (status.isClean())
        {
            LOGGER.info("Das Repository ist sauber. Es gibt nichts zu tun.");
            return;
        }

        final Set<String> added = status.getAdded();
        final Set<String> changed = status.getChanged();
        final Set<String> conflicting = status.getConflicting();
        final Set<String> ignoredNotInIndex = status.getIgnoredNotInIndex();
        final Set<String> missing = status.getMissing();
        final Set<String> modified = status.getModified(); // was ist der Unterschied zu changed???
        final Map<String, IndexDiff.StageState> conflictingStageState = status.getConflictingStageState();
        final Set<String> removed = status.getRemoved();
        final Set<String> uncommittedChanges = status.getUncommittedChanges();
        final Set<String> untracked = status.getUntracked();

        loggStatusSet("Added",added);
        loggStatusSet("Changed",changed);
        loggStatusSet("Conflicting",conflicting);
        loggStatusSet("Ignored not in Index",ignoredNotInIndex);
        loggStatusSet("Missing",missing);
        loggStatusSet("Modified",modified);
        loggStatusSet("Removed",removed);
        loggStatusSet("Uncommitted changes",uncommittedChanges);
        loggStatusSet("Untracked",untracked);
    }

    private void loggStatusSet(String title, Set<String> stringSet)
    {
        LOGGER.info("==============================================================================");
        LOGGER.info(String.format("%s:",title));
        LOGGER.info("------------------------------------------------------------------------------");
        for (String item : stringSet)
        {
            LOGGER.info(String.format("   %s", item));
        }
    }
}
