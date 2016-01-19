package de.hoepmat.web.controller;

import de.hoepmat.LockFileService;
import de.hoepmat.web.model.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by hoepmat on 1/19/16.
 */
@RestController
public class MaintainRequest {

    @Autowired
    private LockFileService lockFileService;

    @RequestMapping("/releaseLock")
    public ResultResponse releaseLock(){
        try {
            lockFileService.releaseLock();
            return new ResultResponse(ResultResponse.Result.SUCCESS, "Lock successful released.");
        } catch (IOException e) {
            e.printStackTrace();
            return new ResultResponse(ResultResponse.Result.ERROR, "Error on releasing the lock. [" + e.getMessage() + "]");
        }
    }
}
