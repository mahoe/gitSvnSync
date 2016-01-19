package de.hoepmat.web;

import de.hoepmat.web.model.State;
import org.springframework.stereotype.Repository;

/**
 * Created by hoepmat on 1/19/16.
 */
@Repository
public class StateHolder {

    private State state;

    public StateHolder() {
        state = new State();
    }

    public StateHolder(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
