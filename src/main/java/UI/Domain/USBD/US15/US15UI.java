package UI.Domain.USBD.US15;

import UI.Domain.USBD.US17.US17UI;
import UI.Menu.MenuItem;
import UI.Menu.Sprint2MenuUI;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class US15UI implements Runnable {

    /**
     * This method is used to register a new workstation.
     */
    @Override
    public void run() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Load existing IDs into Sets for validation
            Set<Integer> existingWorkstationIds = loadExistingIds(statement, "SELECT Workstation_ID FROM Workstation", "Workstation_ID");

            Utils.clearConsole();
            System.out.println(Utils.BOLD + Utils.CYAN +
                    "\n\n--- Register a Workstation ---------------------------" + Utils.RESET);

            // Input for Workstation ID
            int newWorkstationId = validateIntegerInput(existingWorkstationIds, "Enter Workstation ID: ",
                    "Error: Workstation ID already exists. Please enter a different Workstation ID.");

            // Input for Workstation Name and Description
            String newWorkstationName = Utils.readLineFromConsole(Utils.BOLD + "Enter Workstation Name: " + Utils.RESET);
            String newWorkstationDescription = Utils.readLineFromConsole(Utils.BOLD + "Enter Workstation Description: " + Utils.RESET);

            // Numeric inputs
            double newWorkstationSetupTime = validateDoubleInput("Enter Workstation Setup Time: ");
            double newWorkstationTime = validateDoubleInput("Enter Workstation Time: ");

            // Input for Plant Floor ID
            int newPlantFloorId = choosePlantFloorID();

            // Input for Workstation Type ID
            String newWorkstationTypeId = chooseWorkstationTypeID();

            // Input for temperature and humidity
            double minTemp = validateDoubleInput("Enter Minimum Temperature: ");
            double maxTemp = validateDoubleInput("Enter Maximum Temperature: ");
            double minHum = validateDoubleInput("Enter Minimum Humidity: ");
            double maxHum = validateDoubleInput("Enter Maximum Humidity: ");

            // Call the Register_Workstation function
            try {
                registerWorkstation(connection, newWorkstationId, newWorkstationName, newWorkstationDescription,
                        newWorkstationSetupTime, newWorkstationTime, newPlantFloorId, newWorkstationTypeId,
                        minTemp, maxTemp, minHum, maxHum);

                System.out.println(Utils.GREEN + "\nWorkstation registered successfully." + Utils.RESET);
                Utils.goBackAndWait();
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to establish a connection to the database.
     * @return Connection
     * @throws SQLException if a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

    /**
     * This method is used to load existing IDs into a Set for validation.
     * @param statement Statement
     * @param query String
     * @param columnLabel String
     * @return Set<Integer>
     * @throws SQLException if a database access error occurs
     */
    private Set<Integer> loadExistingIds(Statement statement, String query, String columnLabel) throws SQLException {
        Set<Integer> ids = new HashSet<>();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            ids.add(resultSet.getInt(columnLabel));
        }
        return ids;
    }

    private int validateIntegerInput(Set<Integer> existingIds, String prompt, String errorMessage) {
        while (true) {
            try {
                String input = Utils.readLineFromConsole(Utils.BOLD + prompt + Utils.RESET);
                assert input != null;
                int inputInt = Integer.parseInt(input);

                if (existingIds.contains(inputInt)) {
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
     * This method is used to validate double inputs.
     * @param prompt String
     * @return double
     */
    private double validateDoubleInput(String prompt) {

        while (true) {
            try {
                String input = Utils.readLineFromConsole(Utils.BOLD + prompt + Utils.RESET);
                assert input != null;
                return Double.parseDouble(input);
            } catch (Exception e) {
                System.err.println("Invalid input. Please enter a valid number.\n");
            }
        }
    }

    /**
     * This method is used to choose a plant floor ID.
     * @return int
     */
    private int choosePlantFloorID() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Query to get activated customers
            String query = "SELECT PLANT_FLOOR_ID FROM PLANT_FLOOR";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String customerId = resultSet.getString("Plant_Floor_ID");
                options.add(new MenuItem("Plant Floor ID: " + customerId, new US17UI()));
            }

            int option;

            while (true){
                option = Utils.showAndSelectIndex(options,
                        Utils.BOLD + "\nChoose Plant Floor ID:\n" + Utils.RESET);

                if (option == -2) {
                    new Sprint2MenuUI().run();
                }

                if ((option >= 0) && (option < options.size())) {
                    String choice = options.get(option).toString();
                    if (!choice.equals("Back")) {
                        Utils.clearConsole();
                        String plantFloorId = choice.split(": ")[1];
                        return Integer.parseInt(plantFloorId);
                    }

                } else {
                    System.out.println(Utils.RED + "\nInvalid option. Please choose a valid Plant Floor ID.\n" + Utils.RESET);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving plant floors: " + e.getMessage());
        }
        return -1;
    }

    /**
     * This method is used to choose a workstation type ID.
     * @return String
     */
    private String chooseWorkstationTypeID() {
        List<MenuItem> options = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Query to get activated customers
            String query = "SELECT WORKSTATION_TYPE_ID FROM WORKSTATION_TYPE";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String customerId = resultSet.getString("Workstation_Type_ID");
                options.add(new MenuItem("Workstation Type ID: " + customerId, new US17UI()));
            }

            int option;

            while (true){
                option = Utils.showAndSelectIndex(options,
                        Utils.BOLD + "\nChoose Workstation Type ID:\n" + Utils.RESET);

                if (option == -2) {
                    new Sprint2MenuUI().run();
                }

                if ((option >= 0) && (option < options.size())) {
                    String choice = options.get(option).toString();

                    if (!choice.equals("Back")) {
                        Utils.clearConsole();
                        String workstationTypeId = choice.split(": ")[1];
                        return workstationTypeId;
                    }

                } else {
                    System.out.println(Utils.RED + "\nInvalid option. Please choose a valid Workstation Type ID.\n" + Utils.RESET);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving workstations types: " + e.getMessage());
        }
        return null;
    }

    /**
     * This method is used to register a new workstation.
     * @param connection Connection
     * @param workstationId int
     * @param workstationName String
     * @param workstationDescription String
     * @param workstationSetupTime double
     * @param workstationTime double
     * @param plantFloorId int
     * @param workstationTypeId String
     * @param minTemp double
     * @param maxTemp double
     * @param minHum double
     * @param maxHum double
     * @throws SQLException if a database access error occurs
     */
    private void registerWorkstation(Connection connection, int workstationId, String workstationName, String workstationDescription,
                                     double workstationSetupTime, double workstationTime, int plantFloorId, String workstationTypeId,
                                     double minTemp, double maxTemp, double minHum, double maxHum) throws SQLException {
        String functionCall = "{? = call Register_Workstation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement callableStatement = connection.prepareCall(functionCall)) {
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.setInt(2, workstationId);
            callableStatement.setString(3, workstationName);
            callableStatement.setString(4, workstationDescription);
            callableStatement.setDouble(5, workstationSetupTime);
            callableStatement.setDouble(6, workstationTime);
            callableStatement.setInt(7, plantFloorId);
            callableStatement.setString(8, workstationTypeId);
            callableStatement.setDouble(9, minTemp);
            callableStatement.setDouble(10, maxTemp);
            callableStatement.setDouble(11, minHum);
            callableStatement.setDouble(12, maxHum);

            callableStatement.execute();
        }
    }
}
