package UI.Menu;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StartMenuUI implements Runnable {
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";

    private void animatedPrint(String message, int delay) {
        for (char c : message.toCharArray()) {
            System.out.print(c);
            try {
                TimeUnit.MILLISECONDS.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }

    private void loadingAnimation() {
        Random random = new Random();
        String loadingMessage = " Initializing CraftFlow ...";
        String[] loadingAnimations = {"|", "/", "-", "\\"};

        for (int i = 0; i < 20; i++) {

            System.out.print("\r" + YELLOW + loadingAnimations[i % loadingAnimations.length] + loadingMessage + RESET);
            try {
                TimeUnit.MILLISECONDS.sleep(100 + random.nextInt(50));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }



    private String generateTitleArt() {
        return """
                +------------------------------+
                |     C R A F T F L O W        |
                +------------------------------+
                """;
    }

    @Override
    public void run() {
        // Clear screen (works on most terminals)
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // Display title with a bit of flair
        System.out.println(CYAN + BOLD + generateTitleArt() + RESET);

        // Animated welcome message
        animatedPrint(GREEN + "Welcome to your favourite Simulation Tool!" + RESET, 50);
        System.out.println();

        // Loading animation
        loadingAnimation();

        boolean success = false;
        while (!success) {
            try {
                // Small pause for dramatic effect
                TimeUnit.MILLISECONDS.sleep(500);

                MainMenuUI mainMenuUI = new MainMenuUI();
                mainMenuUI.run();
                success = true;
            } catch (Exception e) {
                System.err.println(RED + "Error: " + e.getMessage() + RESET);

                // Option to retry or exit
                System.out.println(YELLOW + "Would you like to try again? (Y/N)" + RESET);
            }
        }
    }
}