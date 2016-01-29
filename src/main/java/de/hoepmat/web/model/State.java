package de.hoepmat.web.model;

import java.util.Date;

/**
 * Created by hoepmat on 1/19/16.
 */
public class State {
    private String message;

    private boolean suspend = false;

    private String reactivateAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuspend() {
        return suspend;
    }

    public void setSuspend(boolean suspend) {
        if(this.suspend && !suspend){
            reactivateAt = new Date().toString();
        } else {
            reactivateAt = null;
        }
        this.suspend = suspend;
    }

    public String getReactivateAt() {
        return reactivateAt;
    }

    public void setReactivateAt(String reactivateAt) {
        this.reactivateAt = reactivateAt;
    }
}
