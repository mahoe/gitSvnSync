package de.hoepmat;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by hoepmat on 11/17/15.
 */
@Service("lockFileService")
public class LockFileService
{
    @Value("${path.to.lock.file}")
    private String path_to_lock_file;

    @Autowired
    private MailService mailService;

    private static final Logger LOGGER = Logger.getLogger(LockFileService.class);

    /**
     * This creates the lock for this process with a double check.
     */
    public String createLock()
    {
        String token = readCurrentToken();

        if (token != null)
        {
            String message = String.format("Ein zweiter Prozess ist aktuell noch am arbeiten... [Token: %s]", token);
            mailService.sendMail("Error on Synchronization", message);

            throw new RuntimeException(message);
        }

        return writeToken();
    }

    private String writeToken()
    {
        StringBuilder result = new StringBuilder();
        File lockFile = new File(path_to_lock_file);
        final String path = lockFile.getParent();

        if (!lockFile.exists())
        {
            try
            {
                lockFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (!lockFile.canWrite())
        {
            throw new RuntimeException(
                    String.format("Das File [%s] kann nicht zum Schreiben geoeffnet werden!", path));
        }

        try
        {
            FileWriter writer = new FileWriter(lockFile);
            result.append(UUID.randomUUID().toString());
            writer.write(result.toString());
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        LOGGER.info("Lock created successful");
        return result.toString();
    }

    private String readCurrentToken()
    {
        File lockFile = getLockFile();

        if (!lockFile.exists())
        {
            return null;
        }

        if (!lockFile.canRead())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Kann 'LockFile' [");
            sb.append(lockFile.getAbsolutePath());
            sb.append(
                    "] nicht lesen. Eventuell habe ich keine Rechte oder es handelt sich nicht um ein reguläres File.");

            throw new RuntimeException(sb.toString());
        }

        try
        {
            final List<String> strings = FileUtils.readLines(lockFile);
            if(CollectionUtils.isEmpty(strings))
            {
                return null;
            }

            if(strings.size()>1){
                LOGGER.warn( String.format("LockFile [%0] enthaelt mehr als eine Zeile!",lockFile.getAbsolutePath()));
            }

            return strings.get(0);
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                    String.format("Fehler beim Lesen der Datei [%0]", lockFile.getAbsolutePath()),e);

        }
    }

    public void releaseLock() throws IOException {
        CharSequence data = "";
        FileUtils.write(getLockFile(), data, false);
        LOGGER.info("Lock removed already");
    }

    private File getLockFile(){
        return FileUtils.getFile(path_to_lock_file);
    }
}
