package UI.Domain.USBD.US17;

import UI.Menu.MenuItem;
import UI.Menu.Sprint2MenuUI;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US17UI implements Runnable {

    /**
     * This method displays the list of customers and allows the user to select a customer to register an order.
     */
    @Override
    public void run() {
        Utils.clearConsole();
        System.out.println(Utils.BOLD + Utils.CYAN + "\n\n--- Register Customer Order ------------\n\n" + Utils.RESET);

        try (Connection connection = getConnection()) {

            // Choose Customer ID
            int customerId = chooseCustomerID();

            // Input and validate order and delivery dates
            Date orderDate = validateDateInput("Enter Order Date (DD-MM-YYYY): ");
            Date deliveryDate = validateDateInput("Enter Delivery Date (DD-MM-YYYY): ");

            // Validate that delivery date is after order date
            while (!deliveryDate.after(orderDate) && !deliveryDate.equals(orderDate)) {
                System.err.println("Delivery date must be after the order date. Please try again.\n");
                deliveryDate = validateDateInput("Enter Delivery Date (DD-MM-YYYY): ");
            }

            // Input location
            String location = Utils.readLineFromConsole(Utils.BOLD + "Enter Delivery Location: " + Utils.RESET);

            // Input products and quantities
            List<String[]> productsAndQuantities = chooseProductsAndQuantities();

            // Call the register function
            int orderId = registerCustomerOrder(connection, customerId, orderDate, deliveryDate, location, productsAndQuantities);
            System.out.println(Utils.GREEN + "\n\nOrder registered successfully. Order ID: " + orderId + Utils.RESET);
            Utils.goBackAndWait();

        } catch (SQLException e) {
            System.err.println(Utils.RED + "Error: " + e.getMessage() + Utils.RESET);
        }
    }

    /**
     * Method to choose a customer ID from a list of active customers in the database.
     * @return The selected Customer ID
     */
    private int chooseCustomerID() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Query to get activated customers
            String query = "SELECT CUSTOMER_ID FROM CUSTOMER WHERE STATUS = 'Activated'";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String customerId = resultSet.getString("Customer_ID");
                options.add(new MenuItem("Customer ID: " + customerId, new US17UI()));
            }

            int option;

            while (true){
                option = Utils.showAndSelectIndex(options,
                        Utils.BOLD + "Choose Customer ID:\n" + Utils.RESET);

                if (option == -2) {
                    new Sprint2MenuUI().run();
                }

                if ((option >= 0) && (option < options.size())) {
                    String choice = options.get(option).toString();

                    if (!choice.equals("Back")) {
                        Utils.clearConsole();
                        String customerID = choice.split(": ")[1];
                        return Integer.parseInt(customerID);
                    }

                } else {
                    System.out.println(Utils.RED + "\nInvalid option. Please choose a valid Customer ID.\n" + Utils.RESET);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving customers: " + e.getMessage());
        }
        return -1; // Return -1 if no customer is chosen
    }

    /**
     * Method to choose products and their quantities.
     * @return A list of arrays containing product ID and quantity
     */
    private List<String[]> chooseProductsAndQuantities() {
        List<String[]> selectedProducts = new ArrayList<>();
        List<MenuItem> productOptions = new ArrayList<>();
        List<String> priorities = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Query to get available products
            String productQuery = "SELECT PRODUCT_ID FROM PRODUCT";
            ResultSet productResultSet = statement.executeQuery(productQuery);

            while (productResultSet.next()) {
                String productId = productResultSet.getString("PRODUCT_ID");
                productOptions.add(new MenuItem("Product ID: " + productId, new US17UI()));
            }

            // Query to get priorities
            String priorityQuery = "SELECT PRIORITY FROM PRIORITY";
            ResultSet priorityResultSet = statement.executeQuery(priorityQuery);

            while (priorityResultSet.next()) {
                priorities.add(priorityResultSet.getString("PRIORITY"));
            }

            int option;
            while (true) {
                option = Utils.showAndSelectIndex(productOptions,
                        "\n" + Utils.BOLD + "Choose a Product:\n" + Utils.RESET);

                if (option == -2) {
                    new Sprint2MenuUI().run();
                }

                if (option >= 0 && option < productOptions.size()) {
                    // Get the selected product
                    String choice = productOptions.get(option).toString();
                    String productId = choice.split(": ")[1];

                    int quantity;
                    while (true) {
                        try {
                            String input = Utils.readLineFromConsole(Utils.BOLD + "Enter Quantity: " + Utils.RESET);
                            assert input != null;
                            quantity = Integer.parseInt(input);

                            if (quantity <= 0) {
                                System.err.println("Quantity must be greater than 0. Please try again.\n");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid quantity. Please enter a valid integer.\n");
                        }
                    }

                    // Let user choose a priority
                    int priorityOption = Utils.showAndSelectIndex(priorities,
                            "\n" + Utils.BOLD + "Choose a Priority:\n" + Utils.RESET);

                    if (priorityOption == -2) {
                        new Sprint2MenuUI().run();
                    }

                    if (priorityOption >= 0 && priorityOption < priorities.size()) {
                        String priority = priorities.get(priorityOption);

                        // Add product, quantity, and priority to the list
                        selectedProducts.add(new String[]{productId, String.valueOf(quantity), priority});
                        System.out.println(Utils.GREEN + "\nProduct ID " + productId +
                                " with quantity " + quantity + " and priority " + priority +
                                " added successfully." + Utils.RESET);

                        // Ask if the user wants to add more products
                        boolean addMore = Utils.confirm("Do you want to add another product? (Y/N)");
                        if (!addMore) {
                            break;
                        }
                    } else {
                        System.out.println(Utils.RED + "\nInvalid option. Please choose a valid Priority." + Utils.RESET);
                    }
                } else {
                    System.out.println(Utils.RED + "\nInvalid option. Please choose a valid Product ID." + Utils.RESET);
                }
            }

        } catch (SQLException e) {
            System.err.println("\nError retrieving products or priorities: " + e.getMessage());
        }

        return selectedProducts; // Return the list of products, quantities, and priorities
    }

    /**
     * Method to register the customer order by calling the PL/SQL function.
     * @return The newly created Customer Order ID
     */
    private int registerCustomerOrder(Connection connection, int customerId, Date orderDate, Date deliveryDate,
                                      String location, List<String[]> productsAndQuantities) throws SQLException {
        String functionCall = "{? = call Register_Customer_Order(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement callableStatement = connection.prepareCall(functionCall)) {
            // Register the return parameter
            callableStatement.registerOutParameter(1, Types.INTEGER);

            // Set input parameters
            callableStatement.setInt(2, customerId);
            callableStatement.setDate(3, new java.sql.Date(orderDate.getTime()));
            callableStatement.setDate(4, new java.sql.Date(deliveryDate.getTime()));
            callableStatement.setString(5, location);

            // Prepare arrays for products, quantities, and priorities
            List<String> productIds = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();
            List<String> priorities = new ArrayList<>();

            for (String[] product : productsAndQuantities) {
                productIds.add(product[0]); // Product ID
                quantities.add(Integer.parseInt(product[1])); // Quantity
                priorities.add(product[2]); // Priority
            }

            callableStatement.setArray(6, createArray(connection, productIds.toArray(new String[0]), "SYS.ODCIVARCHAR2LIST"));
            callableStatement.setArray(7, createArray(connection, quantities.toArray(new Integer[0]), "SYS.ODCINUMBERLIST"));
            callableStatement.setArray(8, createArray(connection, priorities.toArray(new String[0]), "SYS.ODCIVARCHAR2LIST"));

            // Execute the function
            callableStatement.execute();

            // Return the Customer Order ID
            return callableStatement.getInt(1);
        }
    }

    /**
     * Utility method to create SQL array for PL/SQL functions.
     */
    private Array createArray(Connection connection, Object[] elements, String sqlType) throws SQLException {
        if (connection.isWrapperFor(oracle.jdbc.OracleConnection.class)) {
            oracle.jdbc.OracleConnection oracleConnection = connection.unwrap(oracle.jdbc.OracleConnection.class);
            return oracleConnection.createOracleArray(sqlType, elements);
        } else {
            throw new UnsupportedOperationException("Connection does not support Oracle-specific methods.");
        }
    }

    /**
     * Utility method to get a database connection.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

    /**
     * Validates a date input in the format "dd-MM-yyyy".
     */
    private Date validateDateInput(String prompt) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);

        while (true) {
            try {
                String input = Utils.readLineFromConsole(Utils.BOLD + prompt + Utils.RESET);

                java.util.Date utilDate = sdf.parse(input);

                return new java.sql.Date(utilDate.getTime());
            } catch (Exception e) {
                System.err.println("Invalid date format. Please use DD-MM-YYYY.\n");
            }
        }
    }

}
