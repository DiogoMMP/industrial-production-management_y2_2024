package UI.Domain.USBD.US28;

import UI.Menu.MenuItem;
import UI.Menu.Sprint3MenuUI;
import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class US28UI implements Runnable {
    @Override
    public void run() {
        getReservedMaterials();
        Utils.goBackAndWait();
    }

    private void getReservedMaterials() {
        String function = "{? = call Get_Reserved_Materials()}"; // Adjust for function call
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(function)) {

            // Register the first parameter as the output for the SYS_REFCURSOR
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);

            // Execute the function
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);

            System.out.println("\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Reserved Materials ------------\n" + Utils.RESET);
            printFormattedTable(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printFormattedTable(ResultSet resultSet) throws SQLException {
        // Create a list to store rows of formatted data
        List<String[]> rows = new ArrayList<>();

        // Iterate through the ResultSet
        while (resultSet.next()) {
            // Read each column from the ResultSet
            String partId = resultSet.getString("PART_ID");
            int quantity = resultSet.getInt("QUANTITY");
            String unit = resultSet.getString("UNIT");
            String description = resultSet.getString("PART_DESCRIPTION");
            String supplierDetails = resultSet.getString("SUPPLIER_DETAILS");

            // Store the row as an array
            String[] formattedRow = {
                    partId,
                    description,
                    String.valueOf(quantity),
                    unit,
                    supplierDetails
            };

            rows.add(formattedRow); // Add the formatted row to the list
        }

        System.out.printf(Utils.BOLD + "%-15s %-50s %-10s %-10s %-30s%n",
                "Part ID", "Description", "Quantity", "Unit", "Supplier Details");
        System.out.println("-".repeat(115) + Utils.RESET); // Horizontal divider

        // Print the rows
        for (String[] row : rows) {
            System.out.printf("%-15s %-50s %-10s %-10s %-30s%n",
                    row[0], row[1], row[2], row[3], row[4]);
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

    /**
     * This method clears the console.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
