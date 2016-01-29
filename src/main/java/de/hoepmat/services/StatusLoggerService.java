package de.hoepmat.services;

import de.hoepmat.common.Constants;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static de.hoepmat.common.Constants.DOUBLE_LINE;
import static de.hoepmat.common.Constants.SIMPLE_LINE;

/**
 * Created by hoepmat on 1/12/16.
 */
@Service("statusLoggerService")
public class StatusLoggerService
{
    @Value("${path.to.syncRepository}")
    private String syncRepositoryPath;

    /** The logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(StatusLoggerService.class.getName());

    public void loggStatus() throws IOException
    {
        Repository syncRepo = new FileRepositoryBuilder().setGitDir(new File(syncRepositoryPath)).build();
        Git git = new Git(syncRepo);

        try
        {
            LOGGER.info(DOUBLE_LINE);
            showCurrentState(git);
            LOGGER.info(SIMPLE_LINE);
            loggAllBranches(git);
            LOGGER.info(SIMPLE_LINE);
            loggLastCommits(git, 10);
            LOGGER.info(DOUBLE_LINE);
        }
        catch (GitAPIException e)
        {
            throw new RuntimeException("Something went wrong on collecting the current status", e);
        }
    }

    private void loggAllBranches(Git git) throws GitAPIException
    {
        LOGGER.info("list of available branches:");
        final List<Ref> refList = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (Ref ref : refList)
        {
            LOGGER.info(String.format("%25s - %s", ref.getObjectId().getName(), ref.getName()));
        }
    }

    private void loggLastCommits(Git git, int numberOfCommits) throws GitAPIException
    {
        LOGGER.info(String.format("Die letzten %d commits sind:", numberOfCommits));
        final LogCommand log = git.log();
        log.setMaxCount(numberOfCommits);

        final Iterable<RevCommit> commits = log.call();
        for (RevCommit commit : commits)
        {
            String shortMsg = cutStringToMax(commit == null ? "" : commit.getShortMessage(), 30);
            final String name = commit==null || commit.getId() == null ? "no commit or commit id?" : commit.getId().getName();
            LOGGER.info(String.format("%25s - %s", name, shortMsg));
        }
    }

    private String cutStringToMax(String string, int maxCharacters)
    {
        int newLength = Math.min(maxCharacters, string.length());
        StringBuilder sb = new StringBuilder(newLength);

        if (string.isEmpty())
        {
            sb.append("no message");
        }
        else
        {
            if (string.length() > maxCharacters)
            {
                sb.append(string.substring(0, newLength - 3));
                sb.append("...");
            }
            else
            {
                sb.append(string.substring(0, newLength));
            }
        }

        return sb.toString();
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

        loggStatusSet("Added", added);
        loggStatusSet("Changed", changed);
        loggStatusSet("Conflicting", conflicting);
        loggStatusSet("Ignored not in Index", ignoredNotInIndex);
        loggStatusSet("Missing", missing);
        loggStatusSet("Modified", modified);
        loggStatusSet("Removed", removed);
        loggStatusSet("Uncommitted changes", uncommittedChanges);
        loggStatusSet("Untracked", untracked);
    }

    private void loggStatusSet(String title, Set<String> stringSet)
    {
        LOGGER.info(Constants.DOUBLE_LINE);
        LOGGER.info(String.format("%s:", title));
        LOGGER.info(Constants.SIMPLE_LINE);
        for (String item : stringSet)
        {
            LOGGER.info(String.format("   %s", item));
        }
    }
}
