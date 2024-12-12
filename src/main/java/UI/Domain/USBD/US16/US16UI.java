package UI.Domain.USBD.US16;

import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;
import java.sql.*;
import java.util.Scanner;

public class US16UI implements Runnable {

    /**
     * This method is called when the US16UI object is created (by calling the run method).
     */
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n\n\033[1m\033[36m--- Register Product ------------\033[0m");

        // Parameters for the add_product function
        String productId;
        String productName;
        String partDescription;
        int factoryPlantId;
        int marketDemand;
        int optimization;
        int productionCost;
        int flexibility;
        int familyId;

        // Get the product ID from the user
        System.out.print("\nEnter the Product ID: ");
        productId = scanner.nextLine();

        // Get the product name from the user
        System.out.print("\nEnter the Product Name: ");
        productName = scanner.nextLine();

        // Get the part description from the user
        System.out.print("\nEnter the Part Description: ");
        partDescription = scanner.nextLine();

        // Get the factory plant ID from the user
        while (true) {
            try {
                System.out.print("\nEnter the Factory Plant ID: ");
                factoryPlantId = Integer.parseInt(scanner.nextLine());
                break; // Exit the loop if the input is valid
            } catch (NumberFormatException e) {
                System.err.println("Invalid Factory Plant ID. Please enter a valid integer.\n");
            }
        }

        // Get the market demand from the user
        while (true) {
            try {
                System.out.print("\nEnter the Market Demand: ");
                marketDemand = Integer.parseInt(scanner.nextLine());
                break; // Exit the loop if the input is valid
            } catch (NumberFormatException e) {
                System.err.println("Invalid Market Demand. Please enter a valid integer.\n");
            }
        }

        // Get the optimization from the user
        while (true) {
            try {
                System.out.print("\nEnter the Optimization: ");
                optimization = Integer.parseInt(scanner.nextLine());
                break; // Exit the loop if the input is valid
            } catch (NumberFormatException e) {
                System.err.println("Invalid Optimization. Please enter a valid integer.\n");
            }
        }

        // Get the production cost from the user
        while (true) {
            try {
                System.out.print("\nEnter the Production Cost: ");
                productionCost = Integer.parseInt(scanner.nextLine());
                break; // Exit the loop if the input is valid
            } catch (NumberFormatException e) {
                System.err.println("Invalid Production Cost. Please enter a valid integer.\n");
            }
        }

        // Get the flexibility from the user
        while (true) {
            try {
                System.out.print("\nEnter the Flexibility: ");
                flexibility = Integer.parseInt(scanner.nextLine());
                break; // Exit the loop if the input is valid
            } catch (NumberFormatException e) {
                System.err.println("Invalid Flexibility. Please enter a valid integer.\n");
            }
        }

        // Get the family ID from the user
        while (true) {
            try {
                System.out.print("\nEnter the Family ID: ");
                familyId = Integer.parseInt(scanner.nextLine());
                break; // Exit the loop if the input is valid
            } catch (NumberFormatException e) {
                System.err.println("Invalid Family ID. Please enter a valid integer.\n");
            }
        }

        // Call the addProduct method and store the result
        String result = addProduct(productId, productName, partDescription, factoryPlantId,
                marketDemand, optimization, productionCost, flexibility, familyId);

        if (result.contains("Error")) {
            System.err.println(result + "\n");  // Print the error message
        } else {
            System.out.println(result + "\n");  // Print the success message
        }

        Utils.goBackAndWait();  // Wait for user input before returning to the main menu
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

        return result;  // Return the result (either success or error message)
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
