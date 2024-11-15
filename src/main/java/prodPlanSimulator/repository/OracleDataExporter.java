package prodPlanSimulator.repository;

import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import prodPlanSimulator.domain.Item;


public class OracleDataExporter {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "SYS as SYSDBA";
    private static final String PASS = "LAPR3";

    public static void main(String[] args) {
        OracleDataExporter exporter = new OracleDataExporter();
//        exporter.exportItemsToCSV("items.csv");
//        exporter.exportOperationsToCSV("operations.csv");
//        exporter.exportBOOToCSV("boo.csv");
//        exporter.exportArticlesToCSV("articles.csv");
//        exporter.exportWorkstationsToCSV("workstations.csv");
        exportTableToCSV("PRODUCT", "src/main/resources/product.csv");
        exportTableToCSV("BOO", "boo.csv");
    }

    public static void exportTableToCSV(String tableName, String csvFilePath) {
        try (
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName );
                CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(csvFilePath), CSVFormat.DEFAULT)
        ) {
            System.out.println("Connected to the database and executing query...");

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }
            csvPrinter.printRecord((Object[]) columnNames);
            System.out.println("Column names written: " + java.util.Arrays.toString(columnNames));

            boolean hasData = false;
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                csvPrinter.printRecord(row);
                System.out.println("Row written: " + java.util.Arrays.toString(row));
                hasData = true;
            }
            if (!hasData) {
                System.out.println("No rows found for table: " + tableName);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

  /*  public void exportItemsToCSV(String fileName) {
        String functionCall = "{ ? = CALL Get_Product_Parts(?) }";
        String getProductIdsQuery = "SELECT Product_ID FROM Product";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement productStmt = conn.prepareStatement(getProductIdsQuery);
             CallableStatement stmt = conn.prepareCall(functionCall);
             FileWriter csvWriter = new FileWriter(fileName)) {

            // Execute query to get all Product IDs
            ResultSet productRs = productStmt.executeQuery();

            boolean headersWritten = false;

            // Loop over each Product ID
            while (productRs.next()) {
                String productID = productRs.getString("Product_ID");

                // Register the output parameter (cursor) and set the input parameter (productID)
                stmt.registerOutParameter(1, Types.REF_CURSOR);
                stmt.setString(2, productID);

                // Execute the function
                stmt.execute();

                // Retrieve the cursor
                ResultSet rs = (ResultSet) stmt.getObject(1);
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Write headers to CSV if not already written
                if (!headersWritten) {
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(metaData.getColumnName(i));
                        if (i < columnCount) csvWriter.append(";");
                    }
                    csvWriter.append("\n");
                    headersWritten = true;
                }

                // Write data rows to CSV
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(rs.getString(i) != null ? rs.getString(i) : "");
                        if (i < columnCount) csvWriter.append(";");
                    }
                    csvWriter.append("\n");
                }
            }

            System.out.println("All product parts data exported to " + fileName);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }



    public void exportOperationsToCSV(String fileName) {
        String query = "SELECT Manufacturing_Operation_ID, Operation_Description FROM Manufacturing_Operation";
        exportToCSV(query, fileName, ";");
    }

    public void exportBOOToCSV(String fileName) {
        String getProductIdsQuery = "SELECT Product_ID FROM Product";
        String functionCall = "{ ? = CALL Get_Product_Operations(?) }";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement productStmt = conn.prepareStatement(getProductIdsQuery);
             CallableStatement stmt = conn.prepareCall(functionCall);
             FileWriter csvWriter = new FileWriter(fileName)) {

            // Execute query to get all Product IDs
            ResultSet productRs = productStmt.executeQuery();
            boolean headersWritten = false;

            // Loop over each Product ID
            while (productRs.next()) {
                String productID = productRs.getString("Product_ID");

                // Register the output parameter (cursor) and set the input parameter (productID)
                stmt.registerOutParameter(1, Types.REF_CURSOR);
                stmt.setString(2, productID);

                // Execute the function
                stmt.execute();

                // Retrieve the cursor
                ResultSet rs = (ResultSet) stmt.getObject(1);
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Write headers to CSV if not already written
                if (!headersWritten) {
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(metaData.getColumnName(i));
                        if (i < columnCount) csvWriter.append(";");
                    }
                    csvWriter.append("\n");
                    headersWritten = true;
                }

                // Write data rows to CSV
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(rs.getString(i) != null ? rs.getString(i) : "");
                        if (i < columnCount) csvWriter.append(";");
                    }
                    csvWriter.append("\n");
                }
            }

            System.out.println("All product operations data exported to " + fileName);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    public void exportArticlesToCSV(String fileName) {
        String getProductIdsQuery = "SELECT Product_ID FROM Product";
        String functionCall = "{ ? = CALL Get_Product_Operations(?) }";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement productStmt = conn.prepareStatement(getProductIdsQuery);
             CallableStatement stmt = conn.prepareCall(functionCall);
             FileWriter csvWriter = new FileWriter(fileName)) {

            // Execute query to get all Product IDs
            ResultSet productRs = productStmt.executeQuery();
            boolean headersWritten = false;

            // Loop over each Product ID
            while (productRs.next()) {
                String productID = productRs.getString("Product_ID");

                // Register the output parameter (cursor) and set the input parameter (productID)
                stmt.registerOutParameter(1, Types.REF_CURSOR);
                stmt.setString(2, productID);

                // Execute the function
                stmt.execute();

                // Retrieve the cursor
                ResultSet rs = (ResultSet) stmt.getObject(1);
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Write headers to CSV if not already written
                if (!headersWritten) {
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(metaData.getColumnName(i));
                        if (i < columnCount) csvWriter.append(";");
                    }
                    csvWriter.append("\n");
                    headersWritten = true;
                }

                // Write data rows to CSV
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(rs.getString(i) != null ? rs.getString(i) : "");
                        if (i < columnCount) csvWriter.append(";");
                    }
                    csvWriter.append("\n");
                }
            }

            System.out.println("All product operations data exported to " + fileName);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    public void exportWorkstationsToCSV(String fileName) {
        String query = "SELECT W.Workstation_ID, MO.Operation_Description, W.Workstation_Time " +
                "FROM Workstation W " +
                "JOIN Type_Industry TI ON W.Workstation_Type_ID = TI.Workstation_Type_ID " +
                "JOIN Manufacturing_Operation MO ON TI.Manufacturing_Operation_ID = MO.Manufacturing_Operation_ID " +
                "ORDER BY W.Workstation_ID";
        exportToCSV(query, fileName, ";");
    }

    private void exportToCSV(String query, String fileName, String delimiter) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             FileWriter csvWriter = new FileWriter(fileName)) {

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write headers to CSV
            for (int i = 1; i <= columnCount; i++) {
                csvWriter.append(metaData.getColumnName(i));
                if (i < columnCount) csvWriter.append(delimiter);
            }
            csvWriter.append("\n");

            // Write data rows to CSV
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(rs.getString(i) != null ? rs.getString(i) : "");
                    if (i < columnCount) csvWriter.append(delimiter);
                }
                csvWriter.append("\n");
            }

            System.out.println("Data exported to " + fileName);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }*/
}
