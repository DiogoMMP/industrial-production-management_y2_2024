package machineSupervisor;

import java.io.*;

public class MachineController {

    public static void machineController() {
        try {
            // Resolve working directory dynamically
            String projectDir = System.getProperty("user.dir");
            java.io.File workingDir = new java.io.File(projectDir, "../sem3-pi-2024-g094/machineSupervisor/ARQCP/SPRINT3/UI");
            String wslWorkingDir = workingDir.getCanonicalPath().replace("\\", "/").replace("C:", "/mnt/c");

            // Command to execute
            String command = String.format("cd %s && make run", wslWorkingDir);

            // Run the compiled program via WSL
            ProcessBuilder progProcessBuilder = new ProcessBuilder("wsl", "bash", "-c", command);
            progProcessBuilder.directory(workingDir);
            progProcessBuilder.redirectErrorStream(true); // Merge stderr and stdout
            Process progProcess = progProcessBuilder.start();

            // Get the output of the C program
            InputStreamReader inputStreamReader = new InputStreamReader(progProcess.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;

            // Read and display the C program's output (options)
            System.out.println("Program Output (Options):");
            boolean optionsDisplayed = false;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                optionsDisplayed = true;
            }

            if (!optionsDisplayed) {
                System.out.println("No options were displayed by the C program. Please check the program output.");
            }

            // Prompt user to select an option if options were displayed
            if (optionsDisplayed) {
                BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter your choice: ");
                String userChoice = userInputReader.readLine();

                // Send the user's choice to the program's input stream
                OutputStream outputStream = progProcess.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                writer.write(userChoice);
                writer.newLine();
                writer.flush();
            }

            // Wait for the process to finish
            progProcess.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        machineController();
    }
}
