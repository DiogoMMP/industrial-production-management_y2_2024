package UI.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Paulo Maio pam@isep.ipp.pt
 */
public class Utils {

    /**
     * ANSI escape codes for colors and styles
     */
    public static final String RESET = "\033[0m";
    public static final String BOLD = "\033[1m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String CYAN = "\033[36m";

    /**
     * Reads a line from the console
     *
     * @param prompt The prompt to show to the user
     * @return The line read from the console
     */
    static public String readLineFromConsole(String prompt) {
        try {
            System.out.print("\n" + prompt);

            InputStreamReader converter = new InputStreamReader(System.in);
            BufferedReader in = new BufferedReader(converter);

            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads a line from the console
     *
     * @param prompt The prompt to show to the user
     * @return The line read from the console
     */
    static public int readIntegerFromConsole(String prompt) {
        do {
            try {
                String input = readLineFromConsole(prompt);

                int value = Integer.parseInt(input);

                return value;
            } catch (NumberFormatException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (true);
    }

    /**
     * Reads a line from the console
     *
     * @param prompt The prompt to show to the user
     * @return The line read from the console
     */
    static public double readDoubleFromConsole(String prompt) {
        do {
            try {
                String input = readLineFromConsole(prompt);

                assert input != null;
                input = input.replace(',', '.');

                double value = Double.parseDouble(input);

                return value;
            } catch (NumberFormatException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (true);
    }

    /**
     * Reads a line from the console
     *
     * @param prompt The prompt to show to the user
     * @return The line read from the console
     */
    static public Date readDateFromConsole(String prompt) {
        do {
            try {
                String strDate = readLineFromConsole(prompt);

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

                Date date = df.parse(strDate);

                return date;
            } catch (ParseException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (true);
    }

   /**
     * Reads a line from the console
     *
     * @param message The prompt to show to the user
     * @return The line read from the console
     */
    static public boolean confirm(String message) {
        String input;
        do {
            input = Utils.readLineFromConsole("\n" + message + "\n");
        } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n"));

        return input.equalsIgnoreCase("y");
    }

    /**
     * Reads a line from the console
     *
     * @param list The list to show to the user
     * @param header The header to show to the user
     * @return The object selected by the user
     */
    static public Object showAndSelectOne(List list, String header) {
        showList(list, header);
        return selectsObject(list);
    }

    /**
     * Reads a line from the console
     *
     * @param list The list to show to the user
     * @param header The header to show to the user
     * @return The index selected by the user
     */
    static public int showAndSelectIndex(List list, String header) {
        showList(list, header);
        return selectsIndex(list);
    }

    /**
     * Reads a line from the console
     *
     * @param list The list to show to the user
     * @param header The header to show to the user
     */
    static public void showList(List list, String header) {
        System.out.println(header);

        int index = 0;
        for (Object o : list) {
            index++;

            System.out.println("  " + index + " - " + o.toString());
        }
        //System.out.println();
        System.out.println("  0 - Exit");
    }

    /**
     * Reads a line from the console
     *
     * @param list The list to show to the user
     * @return The object selected by the user
     */
    static public Object selectsObject(List list) {
        String input;
        int value;
        do {
            input = Utils.readLineFromConsole("Type your option: ");
            value = Integer.valueOf(input);
        } while (value < 0 || value > list.size());

        if (value == 0) {
            return null;
        } else {
            return list.get(value - 1);
        }
    }

    /**
     * Reads a line from the console
     *
     * @param list The list to show to the user
     * @return The index selected by the user
     */
    static public int selectsIndex(List list) {
        String input;
        int value;
        do {
            input = Utils.readLineFromConsole("Type your option: ");

            try {
                value = Integer.valueOf(input);

                if (value < 0 || value > list.size()) {
                    System.err.println("Invalid option. Please try again.");
                    return -1;
                }

            } catch (NumberFormatException ex) {
                value = -1;
            }
        } while (value < 0 || value > list.size());

        return value - 1;
    }

    /**
     * Reads a line from the console
     */
    static public void goBackAndWait() {
        String input;
        do {
            input = Utils.readLineFromConsole("Press '0' to go back: ");
        } while (!Objects.equals(input, "0"));
    }

    /**
     * Opens the specified file in the default browser.
     *
     * @param file The file to open.
     * @author Diogo Pereira
     */
    public static void openInBrowser(File file) {
        try {
            if (!file.exists()) {
                System.err.println("File does not exist: " + file.getAbsolutePath());
                return;
            }

            String os = System.getProperty("os.name").toLowerCase();
            String filePath = file.getAbsolutePath();

            if (os.contains("win")) {
                // Windows: uses the ‘start’ command for the default browser
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "chrome", filePath});
            } else if (os.contains("mac")) {
                // MacOS: uses the ‘open’ command for the default browser
                Runtime.getRuntime().exec(new String[]{"open", "-a", "Google Chrome", filePath});
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux: uses the ‘xdg-open’ command for the default browser
                Runtime.getRuntime().exec(new String[]{"xdg-open", filePath});
            } else {
                System.err.println("Unsupported OS: " + os);
            }
        } catch (IOException e) {
            System.err.println("Error opening file in the browser: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    /**
     * Opens the specified file in Microsoft Excel, Notepad, or the default text editor.
     *
     * @param file The file to open.
     * @author Diogo Pereira
     */
    public static void openInExcel(File file) {
        try {
            if (!file.exists()) {
                System.err.println("File does not exist: " + file.getAbsolutePath());
                return;
            }

            String os = System.getProperty("os.name").toLowerCase();
            String filePath = file.getAbsolutePath();

            if (os.contains("win")) {
                // Windows: try to open with Excel
                try {
                    // Verify if Excel is installed
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "excel", filePath});
                } catch (IOException e) {
                    // If it fails, open with Notepad
                    System.err.println("Excel not found, opening with Notepad...");
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "notepad", filePath});
                }
            } else if (os.contains("mac")) {
                // MacOS: try to open with Excel
                try {
                    Runtime.getRuntime().exec(new String[]{"open", "-a", "Microsoft Excel", filePath});
                } catch (IOException e) {
                    // If it fails, open with the default editor
                    System.err.println("Excel not found, opening with default editor...");
                    Runtime.getRuntime().exec(new String[]{"open", filePath});
                }
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux: try to open with the default text editor
                try {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", filePath});
                } catch (IOException e) {
                    // If it fails, open with the default text editor
                    System.err.println("Error opening file, opening with default text editor...");
                    Runtime.getRuntime().exec(new String[]{"xdg-open", filePath});
                }
            } else {
                System.err.println("Unsupported OS: " + os);
            }
        } catch (IOException e) {
            System.err.println("Error opening file in the editor: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    /**
     * Opens the specified file in Visual Studio Code.
     *
     * @param file The file to open.
     * @author Diogo Pereira
     */
    public static void openInVSCode(File file) {
        try {
            if (!file.exists()) {
                System.err.println("File does not exist: " + file.getAbsolutePath());
                return;
            }

            String os = System.getProperty("os.name").toLowerCase();
            String filePath = file.getAbsolutePath();

            if (os.contains("win")) {
                // Windows: usa o comando 'code' para abrir o VS Code
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "code", filePath});
            } else if (os.contains("mac")) {
                // MacOS: usa o comando 'code' para abrir o VS Code
                Runtime.getRuntime().exec(new String[]{"code", filePath});
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux: usa o comando 'code' para abrir o VS Code
                Runtime.getRuntime().exec(new String[]{"code", filePath});
            } else {
                System.err.println("Unsupported OS: " + os);
            }
        } catch (IOException e) {
            System.err.println("Error opening file in Visual Studio Code: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }
}