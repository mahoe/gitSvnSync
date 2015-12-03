package de.hoepmat;

import java.io.File;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

/**
 * Created by hoepmat on 11/17/15.
 */
@Service(MailService.BEAN_NAME)
public class MailServiceImpl implements MailService
{
    /** The logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(MailServiceImpl.class.getName());

    @Override
    public void sendMail(String subject, String body)
    {
        LOGGER.info(String.format("send mail subject:[ %s ] - body: [ %s ]", subject, body));
    }

    @Override
    public void sendMail(String mailTo, String mailCc, String mailBcc, String subject, String body,
                         File attachement)
    {

    }
}
