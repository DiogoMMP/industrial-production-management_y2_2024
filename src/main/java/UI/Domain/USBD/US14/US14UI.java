package UI.Domain.USBD.US14;

import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class US14UI implements Runnable {

    /**
     * This method is responsible for running the User Story 14 UI.
     */
    @Override
    public void run() {

        System.out.print(Utils.BOLD + Utils.CYAN +
                "\n\n --- Product Using All Types of Machines ------------" + Utils.RESET);

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            if (Utils.confirm(Utils.BOLD + "Do you want to insert test data? (Y/N)" + Utils.RESET)) {
                insertTestData(statement);
            }

            executeQuery(statement);
            Utils.goBackAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for getting a connection to the database.
     * @return Connection
     * @throws SQLException if a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

    /**
     * This method is responsible for inserting test data into the database.
     * @param statement the statement to be used to execute the SQL statements
     * @throws SQLException if a database access error occurs
     */
    private void insertTestData(Statement statement) throws SQLException {
        String[] insertStatements = {
                "INSERT INTO Part (Part_ID, Part_Description) VALUES ('NEWPROD001', 'Description for NEWPROD001')",
                "INSERT INTO Internal_Part (Part_ID) VALUES ('NEWPROD001')",
                "INSERT INTO Product (Product_ID, Product_Name, Factory_Plant_ID, Family_ID) VALUES ('NEWPROD001', 'All Workstations Pot', 1, 125)",
                "INSERT INTO Operation_Type (Operation_Type_ID, Operation_Description) VALUES (5672, 'Packaging for large items')",
                "INSERT INTO Operation_Type (Operation_Type_ID, Operation_Description) VALUES (5673, 'Circular glass cutting')",
                "INSERT INTO Operation_Type (Operation_Type_ID, Operation_Description) VALUES (5674, 'Glass trimming')",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5001, 5647)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5002, 5649)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5003, 5651)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5004, 5653)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5005, 5655)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5006, 5657)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5007, 5659)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5008, 5661)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5009, 5663)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5010, 5665)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5011, 5667)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5012, 5669)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5013, 5671)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5014, 5672)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5015, 5673)",
                "INSERT INTO Operation (Operation_ID, Operation_Type_ID) VALUES (5016, 5674)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5001, 5002)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5002, 5003)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5003, 5004)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5004, 5005)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5005, 5006)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5006, 5007)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5007, 5008)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5008, 5009)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5009, 5010)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5010, 5011)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5011, 5012)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5012, 5013)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5013, 5014)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5014, 5015)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5015, 5016)",
                "INSERT INTO BOO (Product_ID, Operation_ID, Next_Operation_ID) VALUES ('NEWPROD001', 5016, NULL)",
                "INSERT INTO Workstation_Type_Operation_Type (Workstation_Type_ID, Operation_Type_ID, Maximum_Execution_Time, Setup_Time) VALUES ('K3676', 5672, 300, 20)",
                "INSERT INTO Workstation_Type_Operation_Type (Workstation_Type_ID, Operation_Type_ID, Maximum_Execution_Time, Setup_Time) VALUES ('G9273', 5673, 150, 15)",
                "INSERT INTO Workstation_Type_Operation_Type (Workstation_Type_ID, Operation_Type_ID, Maximum_Execution_Time, Setup_Time) VALUES ('G9274', 5674, 200, 25)"
        };

        for (String insertStatement : insertStatements) {
            statement.executeUpdate(insertStatement);
        }
    }

    /**
     * This method is responsible for executing the query to retrieve the products that use all types of machines.
     * @param statement the statement to be used to execute the SQL statements
     * @throws SQLException if a database access error occurs
     */
    private void executeQuery(Statement statement) throws SQLException {
        String query = "SELECT p.Product_ID, p.Product_Name " +
                "FROM Product p " +
                "WHERE p.Product_ID IN ( " +
                "    SELECT b.Product_ID " +
                "    FROM BOO b " +
                "    JOIN Operation o ON b.Operation_ID = o.Operation_ID " +
                "    JOIN Workstation_Type_Operation_Type wtot ON o.Operation_Type_ID = wtot.Operation_Type_ID " +
                "    GROUP BY b.Product_ID " +
                "    HAVING COUNT(DISTINCT wtot.Workstation_Type_ID) = ( " +
                "        SELECT COUNT(DISTINCT Workstation_Type_ID) FROM Workstation_Type " +
                "    ) " +
                ")";

        try (ResultSet resultSet = statement.executeQuery(query)) {

            if (!resultSet.isBeforeFirst()) {
                System.err.println("\nNo products found using all types of machines");
                return;
            }

            System.out.printf(Utils.BOLD + "%n%-20s %-50s%n", "Product ID", "Product Name");
            System.out.println("-".repeat(70) + Utils.RESET);

            while (resultSet.next()) {
                String productId = resultSet.getString("Product_ID");
                String productName = resultSet.getString("Product_Name");

                System.out.printf("%-20s %-50s%n", productId, productName);
            }
        }
    }
}