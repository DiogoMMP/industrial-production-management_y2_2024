package UI.Domain.USBD.US08;

import UI.Domain.USBD.US07.US7UI;
import UI.Menu.MenuItem;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;

public class US8UI implements Runnable {

    /**
     * This method runs the user story 8.
     */
    @Override
    public void run() {
        String query = "SELECT DISTINCT " +
                "O.Operation_ID AS Manufacturing_Operation_ID, " +
                "O.Operation_Type_ID AS Manufacturing_Operation_Type_ID, " +
                "OT.Operation_Description AS Manufacturing_Operation_Description, " +
                "WTOT.Workstation_Type_ID, " +
                "WT.Workstation_Type AS Workstation_Type_Description " +
                "FROM " +
                "Operation O " +
                "JOIN Operation_Type OT " +
                "ON O.Operation_Type_ID = OT.Operation_Type_ID " +
                "JOIN Workstation_Type_Operation_Type WTOT " +
                "ON O.Operation_Type_ID = WTOT.Operation_Type_ID " +
                "JOIN Workstation_Type WT " +
                "ON WTOT.Workstation_Type_ID = WT.Workstation_Type_ID " +
                "JOIN Workstation W " +
                "ON WT.Workstation_Type_ID = W.Workstation_Type_ID " +
                "ORDER BY " +
                "O.Operation_ID ";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            Utils.clearConsole();
            System.out.println(Utils.BOLD + Utils.CYAN + "\n\n--- Supported Operations ---" + Utils.RESET);

            System.out.printf(Utils.BOLD + "%n%-20s %-20s %-30s %-25s %-40s%n",
                    "Operation ID", "Operation Type ID", "Operation Description", "Workstation Type ID", "Workstation Type Description");
            System.out.println("-".repeat(155) + Utils.RESET); // Horizontal line

            while (resultSet.next()) {
                int operationId = resultSet.getInt("Manufacturing_Operation_ID");
                int operationTypeId = resultSet.getInt("Manufacturing_Operation_Type_ID");
                String operationDescription = resultSet.getString("Manufacturing_Operation_Description");
                String workstationTypeId = resultSet.getString("Workstation_Type_ID");
                String workstationTypeDescription = resultSet.getString("Workstation_Type_Description");

                System.out.printf("%-20d %-20d %-30s %-25s %-40s%n",
                        operationId, operationTypeId, operationDescription, workstationTypeId, workstationTypeDescription);
            }

            Utils.goBackAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method creates a connection to the database.
     *
     * @return The connection to the database.
     * @throws SQLException If an error occurs while connecting to the database.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }
}