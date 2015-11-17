package de.hoepmat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
            String message = String.format("Ein zweiter Prozess ist aktuell noch am arbeiten... [PID: %0]", token);
            mailService.sendMail("Error on Synchronization", message);

            throw new RuntimeException(message);
        }

        return writeToken();
        //            lockPID=$(cat $PATH_TO_LOCK_FILE)
        //            if [ -z $lockPID ]
        //            then
        //            echo "$$" >> $PATH_TO_LOCK_FILE
        //            else
        //            logecho "$0 ERROR: this script is running in a different thread!"
        //            mail2UsersWithErrorNoAndExit $ERR_LOCK_STILL_PRESENT
        //            fi
        //
        //            #echo "007" >> $PATH_TO_LOCK_FILE
        //
        //            # only to make sure we have this lock allready... (and no other was in between creating a lock as well!)
        //            tmp=$(cat $PATH_TO_LOCK_FILE)
        //            if [ "$tmp" == $$ ]
        //            then
        //            logecho "The lock to protect the process was successfully created."
        //            else
        //            logecho "$0 ERROR: this script is running in a different thread! (started at the same time...)"
        //            mail2UsersWithErrorNoAndExit $ERR_STARTED_AT_THE_SAME_TIME
        //            fi
        //        }
    }

    private String writeToken()
    {
        StringBuilder result = new StringBuilder();
        File lockFile = new File(path_to_lock_file);
        final String path = lockFile.getAbsolutePath();

        if (!lockFile.exists())
        {
            if (!new File(path).mkdirs())
            {
                throw new RuntimeException(String.format("Kann den Pfad [%0] nicht anlegen!", path));
            }
            lockFile.mkdirs();
        }

        if (!lockFile.canWrite())
        {
            throw new RuntimeException(
                    String.format("Das File [%0] kann nicht zum Schreiben geoeffnet werden!", path));
        }

        try
        {
            FileWriter writer = new FileWriter(lockFile);
            result.append(UUID.randomUUID().toString());
            writer.write(result.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    private void releaseLock()
    {
        CharSequence data = "";
        try
        {
            FileUtils.write(getLockFile(), data, false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private File getLockFile(){
        return FileUtils.getFile(path_to_lock_file);
    }
}
