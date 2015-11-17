package de.hoepmat;

import java.io.File;

/**
 * Created by hoepmat on 11/17/15.
 */
public interface MailService
{
    String BEAN_NAME = "mailService";
    void sendMail(String subject, String body);
    void sendMail(String mailTo, String mailCc, String mailBcc, String subject, String body, File attachement);
}
