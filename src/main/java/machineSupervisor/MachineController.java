package machineSupervisor;

import java.io.IOException;

public class MachineController {
    public static void main(String[] args) {
        try {
            // Resolve working directory dynamically
            String projectDir = System.getProperty("user.dir");  // Current Java project directory
            java.io.File workingDir = new java.io.File(projectDir, "../sem3-pi-2024-g094/machineSupervisor/ARQCP/SPRINT3/UI");
            String wslWorkingDir = workingDir.getCanonicalPath().replace("\\", "/").replace("C:", "/mnt/c");

            System.out.println("Working Directory: " + workingDir.getCanonicalPath());
            System.out.println("WSL Path: " + wslWorkingDir);

            // Step 1: Run 'make' to build the program
            ProcessBuilder makeProcessBuilder = new ProcessBuilder(
                    "wsl", "bash", "-c", "cd " + wslWorkingDir + " && make"  // Run 'make run' command in WSL
            );
            makeProcessBuilder.directory(workingDir);  // Ensure 'make' is run in the correct directory
            Process makeProcess = makeProcessBuilder.start();

            // Print the output of 'make'
            Thread outputThread = new Thread(() -> {
                try (var inputStream = makeProcess.getInputStream()) {
                    int readByte;
                    while ((readByte = inputStream.read()) != -1) {
                        System.out.print((char) readByte);
                        System.out.flush(); // Force flush after each print
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Print error stream of 'make'
            Thread errorThread = new Thread(() -> {
                try (var errorStream = makeProcess.getErrorStream()) {
                    int readByte;
                    while ((readByte = errorStream.read()) != -1) {
                        System.err.print((char) readByte);
                        System.err.flush(); // Force flush after each print
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Start both threads
            outputThread.start();
            errorThread.start();

            // Wait for the 'make' process to finish
            int makeExitCode = makeProcess.waitFor();

            // Ensure the threads complete
            outputThread.join();
            errorThread.join();

            if (makeExitCode == 0) {
                System.out.println("'make' executed successfully.");

                // Step 2: Run the compiled program via WSL
                ProcessBuilder progProcessBuilder = new ProcessBuilder(
                        "wsl", "bash", "-c", "cd " + wslWorkingDir + " && make run"  // Run 'make run' command in WSL
                );
                progProcessBuilder.directory(workingDir);  // Make sure we use the right working directory

                Process progProcess = progProcessBuilder.start();

                // Handle program output and errors
                Thread progOutputThread = new Thread(() -> {
                    try (var inputStream = progProcess.getInputStream()) {
                        int readByte;
                        while ((readByte = inputStream.read()) != -1) {
                            System.out.print((char) readByte);
                            System.out.flush(); // Force flush after each print
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
                            System.err.flush(); // Force flush after each print
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

                // Ensure the threads complete
                progOutputThread.join();
                progErrorThread.join();
            } else {
                System.out.println("'make' failed with exit code: " + makeExitCode);
            }

            // Step 3: Run 'make clean' after the program finishes (whether it was successful or not)
            ProcessBuilder cleanProcessBuilder = new ProcessBuilder(
                    "wsl", "bash", "-c", "cd " + wslWorkingDir + " && make clean"  // Run 'make clean' command in WSL
            );
            cleanProcessBuilder.directory(workingDir);  // Ensure we're in the correct directory

            Process cleanProcess = cleanProcessBuilder.start();

            // Print the output of 'make clean'
            Thread cleanOutputThread = new Thread(() -> {
                try (var inputStream = cleanProcess.getInputStream()) {
                    int readByte;
                    while ((readByte = inputStream.read()) != -1) {
                        System.out.print((char) readByte);
                        System.out.flush(); // Force flush after each print
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Print error stream of 'make clean'
            Thread cleanErrorThread = new Thread(() -> {
                try (var errorStream = cleanProcess.getErrorStream()) {
                    int readByte;
                    while ((readByte = errorStream.read()) != -1) {
                        System.err.print((char) readByte);
                        System.err.flush(); // Force flush after each print
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Start the clean output/error threads
            cleanOutputThread.start();
            cleanErrorThread.start();

            // Wait for the clean process to finish
            int cleanExitCode = cleanProcess.waitFor();
            System.out.println("'make clean' exited with code: " + cleanExitCode);

            // Ensure the threads complete
            cleanOutputThread.join();
            cleanErrorThread.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
