package de.hoepmat;

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by hoepmat on 11/17/15.
 */
@Service(MailService.BEAN_NAME)
public class MailServiceImpl implements MailService
{
    /** The logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(MailServiceImpl.class.getName());

    @Value("${smtp.host}")
    private String smtpHost;

    @Value("${smtp.from}")
    private String smtpFrom;

    @Override
    public void sendMail(String subject, String body)
    {
        LOGGER.info(String.format("send mail subject:[ %s ] - body: [ %s ]", subject, body));

        Email email = new SimpleEmail();
        email.setHostName(smtpHost);
        try
        {
            email.setSubject(subject);
            email.setFrom(smtpFrom);
            email.setMsg(body);
            email.addTo("mahoe007@gmail.com");
            email.send();
        }
        catch (EmailException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMail(String mailTo, String mailCc, String mailBcc, String subject, String body,
                         File attachement)
    {

    }
}
