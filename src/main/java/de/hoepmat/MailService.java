package de.hoepmat;

import java.io.File;
import java.util.Set;

/**
 * Created by hoepmat on 11/17/15.
 */
public interface MailService
{
    String BEAN_NAME = "mailService";
    void sendMail(String subject, String body);

    void sendErrorMessage(String message);

    void sendMail(Set<String> mailTo, Set<String> mailCc, Set<String> mailBcc, String subject, String body,
                  File attachement);
}
