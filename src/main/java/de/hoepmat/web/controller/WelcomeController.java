package de.hoepmat.web.controller;

import de.hoepmat.web.StateHolder;
import de.hoepmat.web.model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * The {@link WelcomeController} prepares the data to be presented if the user enters the welcome page.
 * Created by hoepfner on 22.04.2016.
 */
@Controller
public class WelcomeController {
    @Value("${application.version}")
    private String applicationVersion;

    @Autowired
    private StateHolder stateHolder;

    @Value("${application.title}")
    private String applicationTitle;

    @Value("${dryRunForDevelopReason}")
    private boolean dryRunForDevelopReason;

    @RequestMapping("/")
    public String welcome(Map<String,Object> model){
        model.put("application_version", applicationVersion);
        final State state = stateHolder.getState();
        String sb = "Message: " + state.getMessage() + "<BR>" +
                "Reactivated at: " + state.getReactivateAt() + "<BR>" +
                "Spend time for last sync: " + state.getTimeSpend();
        model.put("application_state", sb);
        model.put("application_title", applicationTitle);
        if (dryRunForDevelopReason) {
            model.put("attention_development", "ACHTUNG DEVELOPMENT MODE!");
            model.put("attention_hint","add property 'dryRunForDevelopReason=false'");
        }
        return "welcome";
    }
}
