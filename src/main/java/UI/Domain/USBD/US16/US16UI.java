package UI.Domain.USBD.US16;

import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;
import java.sql.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

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
            Set<Integer> existingFactoryPlantsIds = loadExistingIds(statement, "SELECT Factory_Plant_ID FROM Factory_Plant", "Factory_Plant_ID");
            Set<Integer> existingFamilyIds = loadExistingIds(statement, "SELECT Family_ID FROM Product_Family", "Family_ID");

            System.out.println(Utils.BOLD + Utils.CYAN +
                    "\n\n--- Register a Product ---------------------------" + Utils.RESET);

            String productId = validateStringInput(existingProductIds, "Enter Product ID: ", "Product ID already exists.");

            String productName = Utils.readLineFromConsole(Utils.BOLD + "Enter Product Name: " + Utils.RESET);
            String partDescription = Utils.readLineFromConsole(Utils.BOLD + "Enter Product Description: " + Utils.RESET);

            int factoryPlantId = validateIntegerInput(existingFactoryPlantsIds, "Enter Factory Plant ID: ", "Factory Plant ID does not exist.");

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

            int familyId = validateIntegerInput(existingFamilyIds, "Enter Family ID: ", "Family ID does not exist.");

            String result = addProduct(productId, productName, partDescription, factoryPlantId,
                    marketDemand, optimization, productionCost, flexibility, familyId);

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

    /**
     * This method validates an integer input against a Set of existing IDs.
     * @param existingIds The Set of existing IDs
     * @param prompt The prompt message
     * @param errorMessage The error message
     * @return The validated integer input
     */
    private int validateIntegerInput(Set<Integer> existingIds, String prompt, String errorMessage) {
        while (true) {
            try {
                String input = Utils.readLineFromConsole(Utils.BOLD + prompt + Utils.RESET);
                assert input != null;
                int inputInt = Integer.parseInt(input);

                if (!existingIds.contains(inputInt)) {
                    System.err.println(errorMessage + "\n");
                } else {
                    return inputInt;
                }

            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a valid number.\n");
            }
        }
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
                              Integer productionCost, Integer flexibility, int familyId) {
        String result = "Unknown error";  // Default result in case of failure

        // The PL/SQL function call format (using ? for output parameter)
        String call = "{ ? = call add_product(?, ?, ?, ?, ?, ?, ?, ?, ?) }";

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

            callableStatement.setInt(10, familyId);  // familyId (index 10)

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
}
