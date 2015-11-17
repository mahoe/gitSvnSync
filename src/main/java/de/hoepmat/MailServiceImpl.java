package de.hoepmat;

import java.io.File;

import org.springframework.stereotype.Service;

/**
 * Created by hoepmat on 11/17/15.
 */
@Service(MailService.BEAN_NAME)
public class MailServiceImpl implements MailService
{
    @Override
    public void sendMail(String subject, String body)
    {

    }

    @Override
    public void sendMail(String mailTo, String mailCc, String mailBcc, String subject, String body,
                         File attachement)
    {

    }
}
