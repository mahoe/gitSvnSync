package de.hoepmat;

import java.util.logging.Logger;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by hoepmat on 1/12/16.
 */
@Service("syncCommitMessageService")
public class SyncCommitMessageService
{
    /** The logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(SyncCommitMessageService.class.getName());

    public String getSyncCommitMessage(Iterable<RevCommit> commitDifference)
            throws GitAPIException, IncorrectObjectTypeException, MissingObjectException
    {
        LOGGER.info(Constants.SIMPLE_LINE);
        LOGGER.info("createSyncCommitMessage()");

        // TODO sync message from file
        // check for a file or entry in the database
        // using a template for a synchronize commit message?

        StringBuilder sb = new StringBuilder();
        for (RevCommit commit : commitDifference)
        {
            final String fullMessage = commit.getFullMessage();
            LOGGER.info("The full message: [" + fullMessage + "]");
            sb.append(fullMessage);
        }

        final String result = StringUtils.replace(sb.toString(), "FORCE", "F_O_R_C_E");

        LOGGER.info("commit message will be: [" + result + "]");
        return result;
    }
}
