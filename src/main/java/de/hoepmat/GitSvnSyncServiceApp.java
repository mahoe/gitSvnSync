package de.hoepmat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GitSvnSyncServiceApp
{
    public static void main(String[] args) {
        SpringApplication.run(GitSvnSyncServiceApp.class,args);
    }
}
