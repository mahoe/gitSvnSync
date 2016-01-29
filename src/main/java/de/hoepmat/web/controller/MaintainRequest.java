package de.hoepmat.web.controller;

import de.hoepmat.services.LockFileService;
import de.hoepmat.web.StateHolder;
import de.hoepmat.web.model.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by hoepmat on 1/19/16.
 */
@RestController()
@RequestMapping("/json")
public class MaintainRequest {

    @Autowired
    private LockFileService lockFileService;

    @Autowired
    private StateHolder stateHolder;

    @RequestMapping("/releaseLock")
    public ResultResponse releaseLock() {
        try {
            lockFileService.releaseLock();
            return new ResultResponse(ResultResponse.Result.SUCCESS, "Lock successful released.");
        } catch (IOException e) {
            e.printStackTrace();
            return new ResultResponse(ResultResponse.Result.ERROR, "Error on releasing the lock. [" + e.getMessage() + "]");
        }
    }

    @RequestMapping("/stop")
    public ResultResponse stopApplication() {
        stateHolder.getState().setSuspend(true);
        return new ResultResponse(ResultResponse.Result.SUCCESS,
                "Synchronization is switched off now. Use '.../start' to switch it on again." +
                        " If there is a synchronization already running I will try to finish ...");
    }

    @RequestMapping("/start")
    public ResultResponse startApplication() {
        stateHolder.getState().setSuspend(false);
        return new ResultResponse(ResultResponse.Result.SUCCESS, "Synchronization is switched on.");
    }
}
