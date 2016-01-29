package de.hoepmat.util;

import de.hoepmat.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by hoepmat on 1/21/16.
 */
@Component
public class CommandShell {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(CommandShell.class.getName());

    @Autowired
    private MailService mailService;

    @Value("${path.to.git.executable}")
    private String pathToGitExecutable;

    @Value("${path.to.syncRepository}")
    private String syncRepositoryPath;

    public ArrayList<String> runCommand(String command) {
        ArrayList<String> result = new ArrayList<String>();
        final Runtime runtime = Runtime.getRuntime();
        try {
            final String commandLine = pathToGitExecutable + " " + command;
            final File workDir =
                    new File(syncRepositoryPath.substring(0, syncRepositoryPath.lastIndexOf('.')));

            final Process process = runtime.exec(commandLine, null, workDir);
            process.waitFor();
            StringBuilder sb = new StringBuilder("Result of command '");
            sb.append(commandLine)
                    .append("' in workdir [")
                    .append(workDir.getAbsolutePath())
                    .append("] was [")
                    .append(process.exitValue())
                    .append("]");
            LOGGER.info(sb.toString());

            getOutputLines(result, process.getErrorStream());

            if (result.size() != 0) {
                if ((process.exitValue() == 0)) {
                    LOGGER.warning(result.toString());
                } else {
                    throw new RuntimeException("Error on running shell command" + StringUtils.collectionToDelimitedString(result, "\n"));
                }
            }

            getOutputLines(result, process.getInputStream());
        } catch (InterruptedException e) {
            mailService.sendErrorMessage(e.getMessage());
            throw new RuntimeException(e.toString());
        } catch (IOException e) {
            mailService.sendErrorMessage(e.getMessage());
            throw new RuntimeException(e.toString());
        }

        return result;
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
