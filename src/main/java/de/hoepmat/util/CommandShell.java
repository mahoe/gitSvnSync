package de.hoepmat.util;

import de.hoepmat.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This class is used to run git commands that are not available with the API.
 *
 * Created by hoepmat on 1/21/16.
 */
@Component
public class CommandShell {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(CommandShell.class.getName());
    public static final String ERROR_CODE_PREFIX = "command returned error: ";

    @Autowired
    private MailService mailService;

    @Value("${path.to.git.executable}")
    private String pathToGitExecutable;

    @Value("${path.to.syncRepository}")
    private String syncRepositoryPath;

    public ArrayList<String> runCommand(String command) {
        LOGGER.info("runCommand('" + command + "')");
        ArrayList<String> error_result = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        final Runtime runtime = Runtime.getRuntime();
        try {
            final String commandLine = pathToGitExecutable + " " + command;
            final File workDir =
                    new File(syncRepositoryPath.substring(0, syncRepositoryPath.lastIndexOf('.')));

            final Process process = runtime.exec(commandLine, null, workDir);

            LOGGER.info("getOutputLines() in error stream");
            getOutputLines(error_result, process.getErrorStream());
            LOGGER.info("getOutputLines() in input stream");
            getOutputLines(result, process.getInputStream());

            LOGGER.info("waitFor()");
            boolean finished = waitForWithTimeout(process,1, TimeUnit.MINUTES);
            if(!finished){
                StringBuilder sb = new StringBuilder();
                sb.append("Error on running shell command\n");
                sb.append(StringUtils.collectionToDelimitedString(error_result, "\n"));
                sb.append(StringUtils.collectionToDelimitedString(result, "\n"));
                throw new RuntimeException( sb.toString());
            }

            final int exitValue = process.exitValue();

            StringBuilder sb = new StringBuilder("Result of command '");
            sb.append(commandLine)
                    .append("' in workdir [")
                    .append(workDir.getAbsolutePath())
                    .append("] was [")
                    .append(exitValue)
                    .append("]");
            LOGGER.info(sb.toString());

            if (error_result.size() != 0) {
                if (exitValue == 0) {
                    LOGGER.warning(error_result.toString());
                    result.addAll(0,error_result);
                } else {
                    sb = new StringBuilder();
                    sb.append("Error on running shell command\n");
                    sb.append(StringUtils.collectionToDelimitedString(error_result, "\n"));
                    throw new RuntimeException( sb.toString());
                }
            }

        } catch (InterruptedException | IOException e) {
            mailService.sendErrorMessage(e.getMessage());
            throw new RuntimeException(e.toString());
        }

        return result;
    }

    private boolean waitForWithTimeout(Process process, long timeout, TimeUnit unit)
            throws InterruptedException {
        long startTime = System.nanoTime();
        long rem = unit.toNanos(timeout);

        do {
            try {
                process.exitValue();
                return true;
            }
            catch (IllegalThreadStateException ex) {
                if (rem > 0)
                    Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
            }
            rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
        } while (rem > 0);
        return false;
    }

    private void getOutputLines(ArrayList<String> result, InputStream inputStream) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            LOGGER.info(line);
            result.add(line);
        }
    }
}
