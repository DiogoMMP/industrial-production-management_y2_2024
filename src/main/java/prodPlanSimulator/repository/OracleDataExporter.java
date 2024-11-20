package prodPlanSimulator.repository;

import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import prodPlanSimulator.domain.Item;


public class OracleDataExporter implements Runnable{

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "SYS as SYSDBA";
    private static final String PASS = "LAPR3";

    @Override
    public void run() {
        exportItemsToCSV("src/main/resources/items_exported.csv");
        exportOperationsToCSV("src/main/resources/operations_exported.csv");
        // exportBOOToCSV("src/main/resources/boo_exported.csv");
        // BOO Still not working
        exportArticlesToCSV("src/main/resources/articles_exported.csv");
        exportWorkstationsToCSV("src/main/resources/workstations_exported.csv");
    }

    private static void exportWorkstationsToCSV(String file) {
        String query =
                "SELECT DISTINCT " +
                        "w.Workstation_ID AS workstation, " +
                        "ot.Operation_Description AS name_oper, " +
                        "w.Workstation_Time AS time " +
                        "FROM Workstation w " +
                        "JOIN Type_Industry ti ON w.Workstation_Type_ID = ti.Workstation_Type_ID " +
                        "JOIN Operation_Type ot ON ti.Operation_Type_ID = ot.Operation_Type_ID " +
                        "ORDER BY w.Workstation_ID, ot.Operation_Description, w.Workstation_Time";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter fileWriter = new FileWriter(file);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withDelimiter(';'))) {

            // Track seen combinations to eliminate duplicates
            Map<String, Integer> seenOperations = new HashMap<>();

            // Write the header
            csvPrinter.printRecord("workstation", "name_oper", "time");

            // Write the data rows
            while (rs.next()) {
                String workstation = rs.getString("workstation");
                String nameOper = rs.getString("name_oper");
                int time = rs.getInt("time");

                // Create a unique key for this combination
                String key = workstation + "-" + nameOper;

                // Skip duplicate operations for the same workstation with identical time
                if (seenOperations.containsKey(key) && seenOperations.get(key) == time) {
                    continue;
                }

                // Write the row to CSV
                csvPrinter.printRecord(workstation, nameOper, time);

                // Mark this combination as seen
                seenOperations.put(key, time);
            }

            csvPrinter.flush();
            System.out.println("Workstations exported successfully to " + file);

        } catch (SQLException | IOException e) {
            System.err.println("Error exporting workstations to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }







    private static void exportArticlesToCSV(String file) {
        String query = "SELECT DISTINCT " +
                "p.Product_ID AS article, " +
                "cop.Product_Priority AS priority, " +
                "ot.Operation_Description AS operation_name, " +
                "b.Operation_ID " + // Include b.Operation_ID for ORDER BY
                "FROM Product p " +
                "JOIN BOO b ON p.Product_ID = b.Product_ID " +
                "JOIN Operation_Type ot ON b.Operation_Type_ID = ot.Operation_Type_ID " +
                "JOIN Customer_Order_Product cop ON p.Product_ID = cop.Product_ID " +
                "ORDER BY p.Product_ID, b.Operation_ID";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter fileWriter = new FileWriter(file);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withDelimiter(';'))) {

            // Maps to store the operations and priority for each product
            Map<String, List<String>> operationsMap = new HashMap<>();
            Map<String, String> priorityMap = new HashMap<>();
            int maxOperations = 0; // Track the maximum number of operations

            // Process the query results
            while (rs.next()) {
                String productId = rs.getString("article");
                String operationName = rs.getString("operation_name");
                String priority = rs.getString("priority");

                // Store the priority for each product
                priorityMap.put(productId, priority);

                // Add the operation to the list of operations for the product, avoiding duplicates
                List<String> operations = operationsMap.computeIfAbsent(productId, k -> new ArrayList<>());
                if (!operations.contains(operationName)) {
                    operations.add(operationName);
                }

                // Update the max operations count
                maxOperations = Math.max(maxOperations, operations.size());
            }

            // Dynamically generate headers based on maxOperations
            List<String> headers = new ArrayList<>();
            headers.add("article");
            headers.add("priority");
            for (int i = 1; i <= maxOperations; i++) {
                headers.add("name_oper" + i);
            }

            // Write the headers
            csvPrinter.printRecord(headers);

            // Write the data for each product
            for (Map.Entry<String, List<String>> entry : operationsMap.entrySet()) {
                String article = entry.getKey();
                List<String> operations = entry.getValue();
                String priority = priorityMap.get(article); // Get the priority for the current product

                // Prepare the CSV row
                List<String> row = new ArrayList<>();
                row.add(article);
                row.add(priority);

                // Add operations to the row
                row.addAll(operations);

                // Fill in any missing operation slots with empty values
                while (row.size() < headers.size()) {
                    row.add("");
                }

                // Write the row to CSV
                csvPrinter.printRecord(row);
            }

            // Flush and close CSVPrinter
            csvPrinter.flush();
            System.out.println("Articles exported successfully to " + file);

        } catch (SQLException | IOException e) {
            System.err.println("Error exporting articles to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }




    public static void exportBOOToCSV(String file) {
        // Maps to hold data
        Map<String, List<String>> operationDetails = new HashMap<>();
        Map<String, List<String>> inputItemDetails = new HashMap<>();
        String operationQuery = "SELECT " +
                "b.Operation_ID AS op_id, " +
                "b.Product_ID AS main_item_id, " +
                "1 AS main_item_qtd," +
                "b.Next_Operation_ID AS next_op_id, " +
                "i.Part_ID AS input_item_id, " +
                "i.Quantity AS input_item_qtd " +
                "FROM " +
                "BOO b " +
                "LEFT JOIN BOO_Input i ON b.Product_ID = i.Product_ID AND b.Operation_ID = i.Operation_ID " +
                "ORDER BY b.Product_ID, b.Operation_ID, i.Part_ID";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            // Fetch operation

            ResultSet operationRS = stmt.executeQuery(operationQuery);

            while (operationRS.next()) {
                String opId = operationRS.getString("Operation_ID");
                String nextOpId = operationRS.getString("Next_Operation_ID");

                // Add operation details
                operationDetails.computeIfAbsent(opId, k -> new ArrayList<>());
                if (nextOpId != null) {
                    operationDetails.get(opId).add(nextOpId + ";1");
                }
            }

            // Fetch input items
            String inputQuery = "SELECT Operation_ID, Part_ID, Quantity FROM BOO_Input";
            ResultSet inputRS = stmt.executeQuery(inputQuery);

            while (inputRS.next()) {
                String opId = inputRS.getString("Operation_ID");
                String inputItemId = inputRS.getString("Part_ID");
                String inputItemQtd = inputRS.getString("Quantity");

                // Add input item details
                inputItemDetails.computeIfAbsent(opId, k -> new ArrayList<>());
                inputItemDetails.get(opId).add(inputItemId + ";" + inputItemQtd);
            }

            // Write to CSV
            try (FileWriter csvWriter = new FileWriter(file)) {
                csvWriter.append("op_id;item_id;item_qtd;(;op1;op_qtd1;...;opN;op_qtdN;);(;item_id1;item_qtd1;...;item_idN;item_qtdN;)\n");

                for (String opId : operationDetails.keySet()) {
                    String itemId = opId; // Assuming operation ID matches the product ID
                    String itemQtd = "1"; // Default quantity

                    // Format operation details
                    StringBuilder opDetails = new StringBuilder("(;");
                    for (String detail : operationDetails.getOrDefault(opId, new ArrayList<>())) {
                        opDetails.append(detail).append(";");
                    }
                    opDetails.append(")");

                    // Format input item details
                    StringBuilder itemDetails = new StringBuilder("(;");
                    for (String detail : inputItemDetails.getOrDefault(opId, new ArrayList<>())) {
                        itemDetails.append(detail).append(";");
                    }
                    itemDetails.append(")");

                    // Write row
                    csvWriter.append(opId).append(";")
                            .append(itemId).append(";")
                            .append(itemQtd).append(";")
                            .append(opDetails).append(";")
                            .append(itemDetails).append("\n");
                }
            }

            System.out.println("BOO V2 data exported successfully to " + file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportOperationsToCSV(String file) {
        String query =
                "SELECT Operation_Type_ID AS op_id, Operation_Description AS op_name " +
                        "FROM Operation_Type";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter csvWriter = new FileWriter(file)) {

            // Write the header
            csvWriter.append("op_id;op_name\n");

            // Write the data rows
            while (rs.next()) {
                String opId = rs.getString("op_id");
                String opName = rs.getString("op_name");

                csvWriter.append(opId).append(";").append(opName).append("\n");
            }

            System.out.println("Operations data exported successfully to " + file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportItemsToCSV(String file) {
        String query =
                "SELECT 'Product' AS item_type, Product_ID AS id_item, Product_Name AS item_name " +
                        "FROM Product " +
                        "UNION ALL " +
                        "SELECT 'Raw Material', CAST(Raw_Material_ID AS VARCHAR2(255)), Raw_Material_Name " +
                        "FROM Raw_Material " +
                        "UNION ALL " +
                        "SELECT 'Part', Part_ID, Part_Description " +
                        "FROM Part " +
                        "UNION ALL " +
                        "SELECT 'Component', Component_ID, Component_Description " +
                        "FROM Component";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter csvWriter = new FileWriter(file)) {

            // Write the header
            csvWriter.append("id_item;item_name\n");

            // Write the data rows
            while (rs.next()) {
                String idItem = rs.getString("id_item");
                String itemName = rs.getString("item_name");

                csvWriter.append(idItem).append(";").append(itemName).append("\n");
            }

            System.out.println("Data exported successfully to " + file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
