package de.hoepmat;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @Value("${smtp.default.recipient}")
    private String smtpDefaultRecipient;

    @Override
    public void sendMail(String subject, String body)
    {
        sendMail(null,null,null,subject,body,null);
    }

    @Override
    public void sendErrorMessage(String message){
        sendMail("Error", message);
    }

    @Override
    public void sendMail(Set<String> mailTo, Set<String> mailCc, Set<String> mailBcc, String subject, String body,
                         File attachement)
    {
        LOGGER.info(String.format("send mail subject:[ %s ] - body: [ %s ]", subject, body));

        Email email = new SimpleEmail();
        email.setHostName(smtpHost);
        try
        {
            email.setSubject(subject);
            email.setFrom(smtpFrom);
            email.setMsg(body);

            LOGGER.info("Add mailTo:");
            if (mailTo != null) {
                for (String mail : mailTo) {
                    LOGGER.info("   " + mail);
                    email.addTo(mail);
                }
            } else {
                LOGGER.info("   " + smtpDefaultRecipient + " (default)");
                email.addTo(smtpDefaultRecipient);
            }

            LOGGER.info("Add mailCc:");
            if (mailCc != null) {
                for (String mail : mailCc) {
                    LOGGER.info("   " + mail);
                    email.addCc(mail);
                }
            }

            LOGGER.info("Add mailBcc:");
            if (mailBcc != null) {
                for (String mail : mailBcc) {
                    LOGGER.info("   " + mail);
                    email.addBcc(mail);
                }
            }

            if(attachement!=null){
                LOGGER.severe("File attachements are not supported at the moment!");
            }

            email.send();
        }
        catch (EmailException e)
        {
            LOGGER.log(Level.SEVERE, "There was a problem sending the mail.", e);
        }
    }
}
