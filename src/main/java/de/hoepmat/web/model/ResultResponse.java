package de.hoepmat.web.model;

/**
 * Created by hoepmat on 1/19/16.
 */
public class ResultResponse {
    private Result result;
    private String message;

    public ResultResponse() {
    }

    public ResultResponse(Result result, String message) {
        this.result = result;
        this.message = message;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum Result {
        SUCCESS, ERROR;
    }
}
