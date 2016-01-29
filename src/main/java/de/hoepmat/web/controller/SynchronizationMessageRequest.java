package de.hoepmat.web.controller;

import de.hoepmat.web.CommitMessageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

/**
 * Created by hoepmat on 1/21/16.
 */
@RestController()
@RequestMapping("/json")
public class SynchronizationMessageRequest {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SynchronizationMessageRequest.class.getName());

    @Autowired
    private CommitMessageHolder commitMessageHolder;

    @RequestMapping("/send/{commitMessage}")
    public void setCommitMessage(@PathVariable String commitMessage){
        LOGGER.info(String.format("set the commit message to [%s]", commitMessage));
        commitMessageHolder.setMessage(commitMessage);
    }
}
