package UI.Menu;

import UI.Utils.Utils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StartMenuUI implements Runnable {

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

            System.out.print("\r" + Utils.YELLOW + loadingAnimations[i % loadingAnimations.length] + loadingMessage + Utils.RESET);
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
        System.out.println(Utils.CYAN + Utils.BOLD + generateTitleArt() + Utils.RESET);

        // Animated welcome message
        animatedPrint(Utils.GREEN + "Welcome to your favourite Simulation Tool!" + Utils.RESET, 50);
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
                System.err.println(Utils.RED + "Error: " + e.getMessage() + Utils.RESET);

                // Option to retry or exit
                System.out.println(Utils.YELLOW + "Would you like to try again? (Y/N)" + Utils.RESET);
            }
        }
    }
}