package de.hoepmat;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.logging.Logger;

/**
 * Created by hoepmat on 1/12/16.
 */
@Service("syncCommitMessageService")
public class SyncCommitMessageService
{
    /** The logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(SyncCommitMessageService.class.getName());

    public String getSyncCommitMessage(final Iterable<RevCommit> commitDifference)
            throws GitAPIException, IncorrectObjectTypeException, MissingObjectException
    {
        LOGGER.info("createSyncCommitMessage()");

        // TODO sync message from file
        // check for a file or entry in the database
        // using a template for a synchronize commit message?

        LOGGER.info(Constants.SIMPLE_LINE);
        StringBuilder sb = new StringBuilder();
        for (RevCommit commit : commitDifference) {
            final String fullMessage = commit.getFullMessage();
            final String msg = String.format("Add message from commit [name: %s; time:%s; message: %s]",
                    commit.getName(),
                    commit.getCommitTime(), fullMessage);
            LOGGER.info(msg);
            sb.append(fullMessage);
        }
        LOGGER.info(Constants.SIMPLE_LINE);

        final String result = StringUtils.replace(sb.toString(), "FORCE", "F_O_R_C_E");

        LOGGER.info("commit message will be: [" + result + "]");
        return result;
    }
}
