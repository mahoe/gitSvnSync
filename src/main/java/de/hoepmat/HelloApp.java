package de.hoepmat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HelloApp {
    public static void main(String[] args) {
        SpringApplication.run(HelloApp.class,args);
    }
}
