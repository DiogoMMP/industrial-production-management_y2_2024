package UI.Domain.USBD.US16;

import UI.Domain.USBD.US17.US17UI;
import UI.Menu.MenuItem;
import UI.Menu.Sprint2MenuUI;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;
import java.sql.*;
import java.util.*;

public class US16UI implements Runnable {

    /**
     * This method is called when the US16UI object is created (by calling the run method).
     */
    @Override
    public void run() {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Load existing IDs into Sets for validation
            Set<String> existingProductIds = loadExistingStrings(statement, "SELECT Product_ID FROM Product", "Product_ID");

            System.out.println(Utils.BOLD + Utils.CYAN +
                    "\n\n--- Register a Product ---------------------------" + Utils.RESET);

            String productId = validateStringInput(existingProductIds, "Enter Product ID: ", "Product ID already exists.");

            String productName = Utils.readLineFromConsole(Utils.BOLD + "Enter Product Name: " + Utils.RESET);
            String partDescription = Utils.readLineFromConsole(Utils.BOLD + "Enter Product Description: " + Utils.RESET);

            int factoryPlantId = chooseFactoryPlantID();

            int marketDemand = 0;
            while (true) {
                try {
                    String input = Utils.readLineFromConsole(Utils.BOLD + "Enter Market Demand: " + Utils.RESET);
                    assert input != null;
                    marketDemand = Integer.parseInt(input);
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Market Demand. Please enter a valid integer.\n");
                }
            }

            int optimization = 0;
            while (true) {
                try {
                    String input = Utils.readLineFromConsole(Utils.BOLD + "Enter Optimization: " + Utils.RESET);
                    assert input != null;
                    optimization = Integer.parseInt(input);
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Optimization. Please enter a valid integer.\n");
                }
            }

            int productionCost = 0;
            while (true) {
                try {
                    String input = Utils.readLineFromConsole(Utils.BOLD + "Enter Production Cost: " + Utils.RESET);
                    assert input != null;
                    productionCost = Integer.parseInt(input);
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Production Cost. Please enter a valid integer.\n");
                }
            }

            int flexibility = 0;
            while (true) {
                try {
                    String input = Utils.readLineFromConsole(Utils.BOLD + "Enter Flexibility: " + Utils.RESET);
                    assert input != null;
                    flexibility = Integer.parseInt(input);
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Flexibility. Please enter a valid integer.\n");
                }
            }

            double production_average_time = 0;
            while (true) {
                try {
                    String input = Utils.readLineFromConsole(Utils.BOLD + "Enter Production Average Time: " + Utils.RESET);
                    assert input != null;
                    production_average_time = Double.parseDouble(input);
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Production Average Time. Please enter a valid double.\n");
                }
            }

            int familyId = chooseFamilyID();

            String result = addProduct(productId, productName, partDescription, factoryPlantId,
                    marketDemand, optimization, productionCost, flexibility, production_average_time, familyId);

            if (result.contains("Error")) {
                System.err.println(result + "\n");  // Print the error message
            } else {
                System.out.println(result + "\n");  // Print the success message
            }

            Utils.goBackAndWait();  // Wait for user input before returning to the main menu

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method loads existing IDs from the database into a Set for validation.
     * @param statement The SQL statement object
     * @param query The SQL query to execute
     * @param columnLabel The column label to retrieve from the result set
     * @return The Set of existing IDs
     * @throws SQLException If a database access error occurs
     */
    private Set<Integer> loadExistingIds(Statement statement, String query, String columnLabel) throws SQLException {
        Set<Integer> ids = new HashSet<>();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            ids.add(resultSet.getInt(columnLabel));
        }
        return ids;
    }

    /**
     * This method loads existing strings from the database into a Set for validation.
     * @param statement The SQL statement object
     * @param query The SQL query to execute
     * @param columnLabel The column label to retrieve from the result set
     * @return The Set of existing strings
     * @throws SQLException If a database access error occurs
     */
    private Set<String> loadExistingStrings(Statement statement, String query, String columnLabel) throws SQLException {
        Set<String> ids = new HashSet<>();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            ids.add(resultSet.getString(columnLabel));
        }
        return ids;
    }

    /**
     * This method validates a string input against a Set of existing IDs.
     * @param existingIds The Set of existing IDs
     * @param prompt The prompt message
     * @param errorMessage The error message
     * @return The validated string input
     */
    private String validateStringInput(Set<String> existingIds, String prompt, String errorMessage) {
        while (true) {
            String input = Utils.readLineFromConsole(Utils.BOLD + prompt + Utils.RESET);
            if (existingIds.contains(input)) {
                System.err.println(errorMessage + "\n");
            } else {
                return input;
            }
        }
    }

    private int chooseFactoryPlantID() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Query to get activated customers
            String query = "SELECT FACTORY_PLANT_ID FROM FACTORY_PLANT";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String customerId = resultSet.getString("Factory_Plant_ID");
                options.add(new MenuItem("Factory Plant ID: " + customerId, new US17UI()));
            }

            int option;

            while (true){
                option = Utils.showAndSelectIndex(options,
                        Utils.BOLD + "\nChoose Factory Plant ID:\n" + Utils.RESET);

                if (option == -2) {
                    new Sprint2MenuUI().run();
                }

                if ((option >= 0) && (option < options.size())) {
                    String choice = options.get(option).toString();

                    if (!choice.equals("Back")) {
                        clearConsole();
                        String factoryPlantId = choice.split(": ")[1];
                        return Integer.parseInt(factoryPlantId);
                    }

                } else {
                    System.out.println(Utils.RED + "\nInvalid option. Please choose a valid Factory Plant ID.\n" + Utils.RESET);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving factory plants: " + e.getMessage());
        }
        return -1;
    }

    private int chooseFamilyID() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Query to get activated customers
            String query = "SELECT FAMILY_ID FROM PRODUCT_FAMILY";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String customerId = resultSet.getString("Family_ID");
                options.add(new MenuItem("Family ID: " + customerId, new US17UI()));
            }

            int option;

            while (true){
                option = Utils.showAndSelectIndex(options,
                        Utils.BOLD + "\nChoose Family ID:\n" + Utils.RESET);

                if (option == -2) {
                    new Sprint2MenuUI().run();
                }

                if ((option >= 0) && (option < options.size())) {
                    String choice = options.get(option).toString();

                    if (!choice.equals("Back")) {
                        clearConsole();
                        String familyId = choice.split(": ")[1];
                        return Integer.parseInt(familyId);
                    }

                } else {
                    System.out.println(Utils.RED + "\nInvalid option. Please choose a valid Family ID.\n" + Utils.RESET);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving families: " + e.getMessage());
        }
        return -1;
    }

    /**
     * This method calls the add_product PL/SQL function to add a new product to the database.
     * @param productId The product ID
     * @param productName The product name
     * @param partDescription The part description
     * @param factoryPlantId The factory plant ID
     * @param marketDemand The market demand
     * @param optimization The optimization
     * @param productionCost The production cost
     * @param flexibility The flexibility
     * @param familyId The family ID
     * @return The result of the function call (success or error message)
     */
    private String addProduct(String productId, String productName, String partDescription,
                              int factoryPlantId, Integer marketDemand, Integer optimization,
                              Integer productionCost, Integer flexibility, Double production_average_time, int familyId) {
        String result = "Unknown error";  // Default result in case of failure

        // The PL/SQL function call format (using ? for output parameter)
        String call = "{ ? = call add_product(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(call)) {

            // Register the output parameter (to get the function result message)
            callableStatement.registerOutParameter(1, Types.VARCHAR);

            // Set input parameters for the add_product function
            callableStatement.setString(2, productId);       // productId (index 2)
            callableStatement.setString(3, productName);     // productName (index 3)
            callableStatement.setString(4, partDescription); // partDescription (index 4)
            callableStatement.setInt(5, factoryPlantId);    // factoryPlantId (index 5)

            // Handle optional parameters (may be null, so default to 0 if null)
            callableStatement.setInt(6, marketDemand != null ? marketDemand : 0); // Optional parameter (index 6)
            callableStatement.setInt(7, optimization != null ? optimization : 0); // Optional parameter (index 7)
            callableStatement.setInt(8, productionCost != null ? productionCost : 0); // Optional parameter (index 8)
            callableStatement.setInt(9, flexibility != null ? flexibility : 0); // Optional parameter (index 9)
            callableStatement.setDouble(10, production_average_time);  // production_average_time (index 10)

            callableStatement.setInt(11, familyId);  // familyId (index 11)

            // Execute the function call
            callableStatement.execute();

            // Retrieve the result of the function (a success/error message)
            result = callableStatement.getString(1);  // Function returns a message in the first parameter

        } catch (SQLException e) {
            result = "Not inserted: " + e.getMessage();
            e.printStackTrace();  // Print stack trace if an error occurs during the call
        }

        if (result.contains("Not inserted")) {
            return Utils.RED + result + Utils.RESET;  // Print the error message
        } else {
            return Utils.GREEN + "\nProduct registered successfully." + Utils.RESET;  // Print the success message
        }
    }

    /**
     * This method establishes a connection to the Oracle database.
     * @return The connection object
     * @throws SQLException If a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
