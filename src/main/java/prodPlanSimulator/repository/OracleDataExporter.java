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


public class OracleDataExporter implements Runnable{

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "SYS as SYSDBA";
    private static final String PASS = "LAPR3";

    private static final String FILE_PATH = "src/main/resources/";
    private static final String FILE_ITEMS_PATH = FILE_PATH + "items_exported.csv";
    private static final String FILE_OPERATIONS_PATH = FILE_PATH + "operations_exported.csv";
    private static final String FILE_BOO_PATH = FILE_PATH + "boo_exported.csv";
    private static final String FILE_ARTICLES_PATH = FILE_PATH + "articles_exported.csv";
    private static final String FILE_WORKSTATIONS_PATH = FILE_PATH + "workstations_exported.csv";

    @Override
    public void run() {
        exportItemsToCSV();
        exportOperationsToCSV();
        exportBOOToCSV();
        exportArticlesToCSV();
        exportWorkstationsToCSV();
    }
    public static void main(String[] args) {
        OracleDataExporter exporter = new OracleDataExporter();
        exporter.run();
    }
    private static void exportWorkstationsToCSV() {
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
             FileWriter fileWriter = new FileWriter(FILE_WORKSTATIONS_PATH);
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

        } catch (SQLException | IOException e) {
            System.err.println("Error exporting workstations to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void exportArticlesToCSV() {
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
             FileWriter fileWriter = new FileWriter(FILE_ARTICLES_PATH);
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

        } catch (SQLException | IOException e) {
            System.err.println("Error exporting articles to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void exportBOOToCSV() {
        String query =
                "WITH BOO_DETAILED AS (" +
                        "  SELECT " +
                        "    BOO.Operation_ID AS operation_id, " +
                        "    BOO_Output.Part_ID AS item_id, " +
                        "    BOO_Output.Quantity AS item_quantity, " +
                        "    BOO.Next_Operation_ID AS next_op, " +
                        "    BOO_Input.Part_ID AS input_part, " +
                        "    BOO_Input.Quantity AS input_quantity " +
                        "  FROM BOO " +
                        "  LEFT JOIN BOO_Output ON BOO.Product_ID = BOO_Output.Product_ID AND BOO.Operation_ID = BOO_Output.Operation_ID " +
                        "  LEFT JOIN BOO_Input ON BOO.Product_ID = BOO_Input.Product_ID AND BOO.Operation_ID = BOO_Input.Operation_ID " +
                        ") " +
                        "SELECT " +
                        "  operation_id, " +
                        "  item_id, " +
                        "  item_quantity, " +
                        "  LISTAGG(DISTINCT next_op, ';') WITHIN GROUP (ORDER BY next_op) AS dependencies, " +
                        "  LISTAGG(input_part || ';' || input_quantity, ';') WITHIN GROUP (ORDER BY input_part) AS inputs " +
                        "FROM BOO_DETAILED " +
                        "GROUP BY operation_id, item_id, item_quantity " +
                        "ORDER BY operation_id";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);
             FileWriter csvWriter = new FileWriter(FILE_BOO_PATH)) {

            csvWriter.append("op_id;item_id;item_qtd;(;op1;op_qtd1;op2;op_qtd2;opN;op_qtdN;);(;item_id1;item_qtd1;item_id2;item_qtd2;item_idN;item_qtdN;)\n");

            while (resultSet.next()) {
                int operationId = resultSet.getInt("operation_id");
                String itemId = resultSet.getString("item_id");
                double itemQuantity = resultSet.getDouble("item_quantity");
                String dependencies = resultSet.getString("dependencies");
                String inputs = resultSet.getString("inputs");

                // Format item quantity with comma as decimal separator
                String formattedQuantity = String.format("%.1f", itemQuantity).replace('.', ',');

                // Format dependencies
                String formattedDependencies = formatDependenciesOrInputs(dependencies, true);

                // Format inputs
                String formattedInputs = formatDependenciesOrInputs(inputs, false);

                // Write the formatted data to the CSV file
                csvWriter.append(String.format("%d;%s;%s;%s;%s\n",
                        operationId, itemId, formattedQuantity, formattedDependencies, formattedInputs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String formatDependenciesOrInputs(String input, boolean isDependencies) {
        if (input == null || input.trim().isEmpty()) {
            return isDependencies ? "(;;;;;)" : "(;;;;;;;)";  // Adjusted for your CSV format
        }

        String[] parts = input.split(";");
        StringBuilder formatted = new StringBuilder("(");
        for (int i = 0; i < (isDependencies ? 5 : 6); i++) {
            if (i < parts.length) {
                formatted.append(parts[i]);
            }
            formatted.append(";");
        }
        formatted.append(")");

        return formatted.toString();
    }

    public static void exportOperationsToCSV() {
        String query =
                "SELECT Operation_Type_ID AS op_id, Operation_Description AS op_name " +
                        "FROM Operation_Type";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter csvWriter = new FileWriter(FILE_OPERATIONS_PATH)) {

            // Write the header
            csvWriter.append("op_id;op_name\n");

            // Write the data rows
            while (rs.next()) {
                String opId = rs.getString("op_id");
                String opName = rs.getString("op_name");

                csvWriter.append(opId).append(";").append(opName).append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportItemsToCSV() {
        String query =
                "SELECT 'Product' AS item_type, Product_ID AS id_item, Product_Name AS item_name " +
                        "FROM Product " +
                        "UNION ALL " +
                        "SELECT 'Raw Material' AS item_type, RM.Part_ID AS id_item, P.Part_Description AS item_name " +
                        "FROM Raw_Material RM " +
                        "JOIN Part P ON RM.Part_ID = P.Part_ID " +
                        "UNION ALL " +
                        "SELECT 'Intermediate Product' AS item_type, IP.Part_ID AS id_item, P.Part_Description AS item_name " +
                        "FROM Intermediate_Product IP " +
                        "JOIN Part P ON IP.Part_ID = P.Part_ID " +
                        "UNION ALL " +
                        "SELECT 'Component' AS item_type, C.Part_ID AS id_item, P.Part_Description AS item_name " +
                        "FROM Component C " +
                        "JOIN Part P ON C.Part_ID = P.Part_ID";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter csvWriter = new FileWriter(FILE_ITEMS_PATH)) {

            // Write the header
            csvWriter.append("id_item;item_name\n");

            // Write the data rows
            while (rs.next()) {
                String idItem = rs.getString("id_item");
                String itemName = rs.getString("item_name");

                csvWriter.append(idItem).append(";").append(itemName).append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
