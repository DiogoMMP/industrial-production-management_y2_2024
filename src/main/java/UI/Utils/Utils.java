package UI.Utils;

import java.io.BufferedReader;
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
        } while (!input.equalsIgnoreCase("s") && !input.equalsIgnoreCase("n"));

        return input.equalsIgnoreCase("s");
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
}