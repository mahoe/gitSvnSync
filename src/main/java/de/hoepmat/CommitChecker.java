package de.hoepmat;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by hoepmat on 11/16/15.
 */
@Component
public class CommitChecker
{
    @Value("${path.to.repository}")
    private String repositoryPath;

    @Value("${branch.name.master}")
    private String master;

    public CommitChecker()
    {
        System.out.println("CommitChecker");
    }

    @Scheduled(fixedDelay = 1000 * 10 )
    public void listNewCommit() throws IOException
    {
        Repository r = new FileRepositoryBuilder().setGitDir(new File(repositoryPath)).build();

        Git git = new Git(r);
        final FetchCommand fetch = git.fetch();
        try
        {
//            final FetchResult fetchResult = fetch.call();
            final Status status = git.status().call();
            final LogCommand log = git.log();
            log.setMaxCount(4);
            final Iterable<RevCommit> commits = log.call();

            for (RevCommit commit : commits)
            {
                System.out.println(commit.getCommitTime() + " - " + commit.getFullMessage());
            }

//            System.out.println(fetchResult.toString());
            System.out.println(status.isClean());
        }
        catch (GitAPIException e)
        {
            System.out.println("das ging wohl nicht...");
            e.printStackTrace();
        }
    }
}
