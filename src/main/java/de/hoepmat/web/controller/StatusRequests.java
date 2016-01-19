package de.hoepmat.web.controller;

import de.hoepmat.web.StateHolder;
import de.hoepmat.web.model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hoepmat on 1/19/16.
 */
@RestController
public class StatusRequests {

    @Autowired
    private StateHolder stateHolder;

    @RequestMapping
    public State currentState(){
        return stateHolder.getState();
    }

}
