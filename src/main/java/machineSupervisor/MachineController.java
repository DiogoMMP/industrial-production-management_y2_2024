package machineSupervisor;

import java.io.IOException;

public class MachineController {
    public static void machineController() {
        try {
            // Resolve working directory dynamically
            String projectDir = System.getProperty("user.dir");  // Current Java project directory
            java.io.File workingDir = new java.io.File(projectDir, "../sem3-pi-2024-g094/machineSupervisor/ARQCP/SPRINT3/UI");
            String wslWorkingDir = "\\\"" + workingDir.getCanonicalPath().replace("\\", "/").replace("C:", "/mnt/c") + "\\\"";

            // Run the compiled program via WSL
            System.out.println("Running the compiled program...");

            ProcessBuilder progProcessBuilder = new ProcessBuilder("wsl", "bash", "-c", "cd " + wslWorkingDir + " && make run");
            progProcessBuilder.directory(workingDir);
            Process progProcess = progProcessBuilder.start();

            // Handle program output and errors
            Thread progOutputThread = new Thread(() -> {
                try (var inputStream = progProcess.getInputStream()) {
                    int readByte;
                    while ((readByte = inputStream.read()) != -1) {
                        System.out.print((char) readByte);
                        System.out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread progErrorThread = new Thread(() -> {
                try (var errorStream = progProcess.getErrorStream()) {
                    int readByte;
                    while ((readByte = errorStream.read()) != -1) {
                        System.err.print((char) readByte);
                        System.err.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Start the program output/error threads
            progOutputThread.start();
            progErrorThread.start();

            // Wait for the program to finish
            int progExitCode = progProcess.waitFor();
            System.out.println("Program exited with code: " + progExitCode);
            progOutputThread.join();
            progErrorThread.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}