package de.hoepmat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
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
 */
@Component
public class CommitChecker
{
    /** The logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(CommitChecker.class.getName());

    @Value("${path.to.repository}")
    private String repositoryPath;

    @Value("${branch.name.master}")
    private String master;

    @Autowired
    private LockFileService lockFileService;

    public CommitChecker()
    {
        LOGGER.info("CommitChecker");
    }

    @Scheduled(fixedDelay = 1000 * 10 )
    public void listNewCommit() throws IOException
    {
        lockFileService.createLock();

        Repository r = new FileRepositoryBuilder().setGitDir(new File(repositoryPath)).build();

        Git git = new Git(r);
        final FetchCommand fetch = git.fetch();
        try
        {
//            final FetchResult fetchResult = fetch.call();
            final Status status = git.status().call();

            final LogCommand log = git.log();
            log.setMaxCount(4);

            LOGGER.info("------------------------------------------------------------------------------");
            LOGGER.info("list of available branches:");
            final List<Ref> refList = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
            for (Ref ref : refList)
            {
                LOGGER.info(String .format("%25s - %s",ref.getObjectId().getName(), ref.getName()));
            }
            LOGGER.info("------------------------------------------------------------------------------");

//            AnyObjectId startCommit = git.checkout().setName("master").call().getObjectId();
//            AnyObjectId currentHead ;
//            log.addRange(startCommit, currentHead);
            final Iterable<RevCommit> commits = log.call();

            for (RevCommit commit : commits)
            {
                System.out.println(commit.getCommitTime() + " - " + commit.getFullMessage());
            }

//            System.out.println(fetchResult.toString());
            System.out.println(status.isClean());
        }
        catch (GitAPIException e) {
            System.out.println("das ging wohl nicht...");
            e.printStackTrace();
        } finally
        {
            lockFileService.releaseLock();
        }
    }
}
