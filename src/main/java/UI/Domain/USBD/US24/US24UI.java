package UI.Domain.USBD.US24;

import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class US24UI implements Runnable {

    /**
     * This method runs the tests for the trigger.
     */
    @Override
    public void run() {
        System.out.println(Utils.BOLD + Utils.CYAN + "\n\n--- Trigger Test UI: Avoid Circular References ---\n" + Utils.RESET);

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Step 1: Insert test data
            System.out.println(Utils.BOLD + Utils.BLUE + "Step 1: Inserting test data...\n" + Utils.RESET);
            insertTestData(statement);

            // Step 2: Run tests
            System.out.println(Utils.BOLD + Utils.BLUE + "\nStep 2: Running tests...\n" + Utils.RESET);
            runTests(statement);

            // Step 3: Cleanup test data
            System.out.println(Utils.BOLD + Utils.BLUE + "\nStep 3: Cleaning up test data...\n" + Utils.RESET);
            cleanUp(statement);

            System.out.println(Utils.GREEN + "\nAll steps completed successfully!" + Utils.RESET);

            Utils.goBackAndWait();

        } catch (SQLException e) {
            System.err.println(Utils.RED + "Error: " + e.getMessage() + Utils.RESET);
        }
    }

    /**
     * This method inserts test data into the database.
     * @param statement The statement object
     * @throws SQLException If a database access error occurs
     */
    private void insertTestData(Statement statement) throws SQLException {
        try {
            statement.executeUpdate("INSERT INTO Part (Part_ID, Part_Description) " +
                    "VALUES ('TEST123', 'Test Product Description')");
            statement.executeUpdate("INSERT INTO Part (Part_ID, Part_Description) " +
                    "VALUES ('IP123', 'Test Part Description')");
            statement.executeUpdate("INSERT INTO Internal_Part (Part_ID) VALUES ('TEST123')");
            statement.executeUpdate("INSERT INTO Internal_Part (Part_ID) VALUES ('IP123')");

            statement.executeUpdate("INSERT INTO Intermediate_Product (Part_ID) VALUES ('IP123')");
            System.out.println(Utils.GREEN + "Inserted Intermediate Product IP123." + Utils.RESET);

            statement.executeUpdate("INSERT INTO Product (Product_ID, Product_Name, Factory_Plant_ID, Family_ID) " +
                    "VALUES ('TEST123', 'Test Product', 1, 125)");
            System.out.println(Utils.GREEN + "Inserted Product TEST123." + Utils.RESET);

            statement.executeUpdate("INSERT INTO Operation (Operation_ID, Operation_Type_ID, Expected_Estimated_Time) " +
                    "VALUES (101, 5647, 120)");
            System.out.println(Utils.GREEN + "Inserted Operation 101." + Utils.RESET);

            statement.executeUpdate("INSERT INTO Operation (Operation_ID, Operation_Type_ID, Expected_Estimated_Time) " +
                    "VALUES (102, 5649, 90)");
            System.out.println(Utils.GREEN + "Inserted Operation 102." + Utils.RESET);

            statement.executeUpdate("INSERT INTO BOO (Operation_ID, Product_ID, Next_Operation_ID) " +
                    "VALUES (101, 'TEST123', 102)");
            System.out.println(Utils.GREEN + "Inserted BOO for Operation 101." + Utils.RESET);

            statement.executeUpdate("INSERT INTO BOO (Operation_ID, Product_ID, Next_Operation_ID) " +
                    "VALUES (102, 'TEST123', 103)");
            System.out.println(Utils.GREEN + "Inserted BOO for Operation 102." + Utils.RESET);

        } catch (SQLException e) {
            System.err.println(Utils.RED + "Error inserting test data: " + e.getMessage() + Utils.RESET);
            throw e;
        }
    }

    /**
     * This method runs the tests for the trigger.
     * @param statement The statement object
     * @throws SQLException If a database access error occurs
     */
    private void runTests(Statement statement) throws SQLException {
        // Test 1: Successful insertion

        System.out.println(Utils.BOLD + "Test 1: Inserting a new input for Product TEST123 with Operation 101 and Part IP123.\n" + Utils.RESET);

        try {
            statement.executeUpdate("INSERT INTO BOO_INPUT (Product_ID, Operation_ID, Part_ID, Quantity, Unit) " +
                    "VALUES ('TEST123', 101, 'IP123', 1, 'unit')");
            System.out.println(Utils.GREEN + "Test 1 Passed: Insertion successful with no circular reference." + Utils.RESET);
        } catch (SQLException e) {
            System.out.println(Utils.RED + "Test 1 Failed: " + e.getMessage() + Utils.RESET);
        }

        boolean confirm = Utils.confirm("Would you like to continue with the next test? (Y/N)");

        if (!confirm) {
            return;
        }

        // Test 2: Unsuccessful insertion

        System.out.println(Utils.BOLD + "\n\nTest 2: Attempting to insert a new input for Product TEST123 " +
                "with Operation 102 and Part TEST123.\n" + Utils.RESET);

        try {
            statement.executeUpdate("INSERT INTO BOO_INPUT (Product_ID, Operation_ID, Part_ID, Quantity, Unit) " +
                    "VALUES ('TEST123', 102, 'TEST123', 1, 'unit')");
            System.out.println(Utils.RED + "Test 2 Failed: No error raised for circular references." + Utils.RESET);
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                System.out.println(Utils.GREEN + "Test 2 Passed: Circular reference error raised.\nMessage: " + e.getMessage() + Utils.RESET);
            } else {
                System.out.println(Utils.RED + "Test 2 Failed: Unexpected error - " + e.getMessage() + Utils.RESET);
            }
        }

        confirm = Utils.confirm("Would you like to continue with the next test? (Y/N)");

        if (!confirm) {
            return;
        }

        // Test 3: Successful update

        System.out.println(Utils.BOLD + "\n\nTest 3: Attempting to update the expected estimated time of Operation 1 to " +
                "20 seconds.\n" + Utils.RESET);

        try {
            statement.executeUpdate("UPDATE BOO_INPUT SET Part_ID = 'IP123' WHERE Operation_ID = 102 " +
                    "AND Product_ID = 'TEST123'");
            System.out.println(Utils.GREEN + "Test 3 Passed: Update successful with no circular reference." + Utils.RESET);
        } catch (SQLException e) {
            System.out.println(Utils.RED + "Test 3 Failed: " + e.getMessage() + Utils.RESET);
        }

        confirm = Utils.confirm("Would you like to continue with the next test? (Y/N)");

        if (!confirm) {
            return;
        }

        // Test 4: Unsuccessful update

        System.out.println(Utils.BOLD + "\n\nTest 4: Attempting to update the expected estimated time of Operation 1 to " +
                "300 seconds (exceeding allowed limit).\n" + Utils.RESET);

        try {
            statement.executeUpdate("UPDATE BOO_INPUT SET Part_ID = 'TEST123' WHERE Operation_ID = 101 " +
                    "AND Product_ID = 'TEST123'");
            System.out.println(Utils.RED + "Test 4 Failed: No error raised for circular references." + Utils.RESET);
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                System.out.println(Utils.GREEN + "Test 4 Passed: Circular reference error raised.\nMessage: " + e.getMessage() + Utils.RESET);
            } else {
                System.out.println(Utils.RED + "Test 4 Failed: Unexpected error - " + e.getMessage() + Utils.RESET);
            }
        }
    }

    /**
     * This method cleans up the test data.
     * @param statement The statement object
     * @throws SQLException If a database access error occurs
     */
    private void cleanUp(Statement statement) throws SQLException {
        try {
            statement.executeUpdate("DELETE FROM BOO_INPUT WHERE Product_ID = 'TEST123'");
            System.out.println(Utils.GREEN + "Cleaned up BOO_INPUT." + Utils.RESET);

            statement.executeUpdate("DELETE FROM BOO WHERE Product_ID = 'TEST123'");
            System.out.println(Utils.GREEN + "Cleaned up BOO." + Utils.RESET);

            statement.executeUpdate("DELETE FROM Intermediate_Product WHERE Part_ID = 'IP123'");
            System.out.println(Utils.GREEN + "Cleaned up Intermediate Product." + Utils.RESET);

            statement.executeUpdate("DELETE FROM Product WHERE Product_ID = 'TEST123'");
            System.out.println(Utils.GREEN + "Cleaned up Product." + Utils.RESET);

            statement.executeUpdate("DELETE FROM Internal_Part WHERE Part_ID = 'IP123'");
            statement.executeUpdate("DELETE FROM Internal_Part WHERE Part_ID = 'TEST123'");
            statement.executeUpdate("DELETE FROM Part WHERE Part_ID = 'IP123'");
            statement.executeUpdate("DELETE FROM Part WHERE Part_ID = 'TEST123'");

            statement.executeUpdate("DELETE FROM Operation WHERE Operation_ID = 101");
            System.out.println(Utils.GREEN + "Cleaned up Operation 101." + Utils.RESET);

            statement.executeUpdate("DELETE FROM Operation WHERE Operation_ID = 102");
            System.out.println(Utils.GREEN + "Cleaned up Operation 102." + Utils.RESET);

        } catch (SQLException e) {
            System.err.println(Utils.RED + "Error cleaning up test data: " + e.getMessage() + Utils.RESET);
            throw e;
        }
    }

    /**
     * This method establishes a connection to the database.
     * @return The connection object
     * @throws SQLException If a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }
}
