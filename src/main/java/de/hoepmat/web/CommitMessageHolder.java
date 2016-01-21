package de.hoepmat.web;

import org.springframework.stereotype.Repository;

/**
 * Holds the message to be used for the merge commit in case of a synchronization.
 *
 * Created by hoepmat on 1/21/16.
 */
@Repository
public class CommitMessageHolder {

    private String message;

    public CommitMessageHolder() {
    }

    public CommitMessageHolder(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
