/*
Utility class to execute shell commands.
*/
package utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandExecutor {

    public boolean executeCommand(String command) throws IOException, InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.environment().put("PATH",
                "C:\\Users\\Puffy\\AppData\\Local\\Google\\Cloud SDK\\google-cloud-sdk\\bin\\" +
                        File.pathSeparator + System.getenv("PATH")
        );

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("bash", "-c", command);
        }

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode + "\nOutput:\n" + output);
        }
        return true;

    }
    public String executeCommandString(String command) throws IOException, InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.environment().put("PATH",
                "C:\\Users\\Puffy\\AppData\\Local\\Google\\Cloud SDK\\google-cloud-sdk\\bin\\" +
                        File.pathSeparator + System.getenv("PATH")
        );

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("bash", "-c", command);
        }

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode + "\nOutput:\n" + output);
        }

        return output.toString().trim();
    }

}
