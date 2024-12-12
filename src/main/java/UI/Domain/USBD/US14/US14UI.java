package UI.Domain.USBD.US14;

import importer_and_exporter.OracleDataExporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class US14UI implements Runnable {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Do you want to insert test data? (yes/no): ");
        String insertTestData = scanner.nextLine();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            if ("yes".equalsIgnoreCase(insertTestData)) {
                insertTestData(statement);
            }

            executeQuery(statement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

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
            while (resultSet.next()) {
                String productId = resultSet.getString("Product_ID");
                String productName = resultSet.getString("Product_Name");

                System.out.printf("Product ID: %s, Product Name: %s%n", productId, productName);
            }
        }
    }
}