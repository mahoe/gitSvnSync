package de.hoepmat.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by hoepfner on 22.04.2016.
 */
@Controller
public class WelcomeController {
    @Value("${application.version}")
    private String applicationVersion;

    @RequestMapping("/")
    public String welcome(Map<String,Object> model){
        model.put("application_version",applicationVersion);
        return "welcome";
    }
}
