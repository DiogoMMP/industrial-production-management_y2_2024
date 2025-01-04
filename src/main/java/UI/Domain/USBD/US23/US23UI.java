package UI.Domain.USBD.US23;

import UI.Utils.Utils;
import importer_and_exporter.OracleDataExporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class US23UI implements Runnable {

    /**
     * This method runs the tests for the trigger.
     */
    @Override
    public void run() {
        Utils.clearConsole();
        System.out.println(Utils.BOLD + Utils.CYAN + "\n\n--- Trigger Test UI: Ensure Operation Time ---\n" + Utils.RESET);

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
            statement.executeUpdate("INSERT INTO Operation_Type (Operation_Type_ID, Operation_Description) " +
                    "VALUES (1, 'Test Operation Type')");
            System.out.println(Utils.GREEN + "Inserted Operation Type 1." + Utils.RESET);

            statement.executeUpdate("INSERT INTO Workstation_Type (Workstation_Type_ID, Workstation_Type) " +
                    "VALUES ('WT1', 'Test Workstation Type')");
            System.out.println(Utils.GREEN + "Inserted Workstation Type WT1." + Utils.RESET);

            statement.executeUpdate("INSERT INTO Workstation_Type_Operation_Type " +
                    "(Workstation_Type_ID, Operation_Type_ID, Maximum_Execution_Time, Setup_Time) " +
                    "VALUES ('WT1', 1, 100, 10)");
            System.out.println(Utils.GREEN + "Workstation type 'WT1' with a maximum execution time of " + Utils.BOLD +
                    " 100 seconds " + Utils.RESET + Utils.GREEN + " was assigned to Operation Type 1 and added successfully." + Utils.RESET);

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

        System.out.println(Utils.BOLD + "Test 1: Attempting to insert an operation with an expected estimated time " +
                "of 90 seconds (within allowed limit).\n" + Utils.RESET);

        try {
            statement.executeUpdate("INSERT INTO Operation (Operation_ID, Operation_Type_ID, Expected_Estimated_Time) " +
                    "VALUES (1, 1, 90)");
            System.out.println(Utils.GREEN + "Test 1 Passed: Insertion within limit." + Utils.RESET);
        } catch (SQLException e) {
            System.out.println(Utils.RED + "Test 1 Failed: " + e.getMessage() + Utils.RESET);
        }

        boolean confirm = Utils.confirm("Would you like to continue with the next test? (Y/N)");

        if (!confirm) {
            return;
        }

        // Test 2: Unsuccessful insertion

        System.out.println(Utils.BOLD + "\n\nTest 2: Attempting to insert an operation with an expected estimated time " +
                "of 200 seconds (exceeding allowed limit).\n" + Utils.RESET);

        try {
            statement.executeUpdate("INSERT INTO Operation (Operation_ID, Operation_Type_ID, Expected_Estimated_Time) " +
                    "VALUES (2, 1, 200)");
            System.out.println(Utils.RED + "Test 2 Failed: No error raised for exceeding limit." + Utils.RESET);
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                System.out.println(Utils.GREEN + "Test 2 Passed: Error raised for time limit.\nMessage: " + e.getMessage() + Utils.RESET);
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
            statement.executeUpdate("UPDATE Operation SET Expected_Estimated_Time = 20 WHERE Operation_ID = 1");
            System.out.println(Utils.GREEN + "Test 3 Passed: Update within limit." + Utils.RESET);
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
            statement.executeUpdate("UPDATE Operation SET Expected_Estimated_Time = 300 WHERE Operation_ID = 1");
            System.out.println(Utils.RED + "Test 4 Failed: No error raised for exceeding limit." + Utils.RESET);
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                System.out.println(Utils.GREEN + "Test 4 Passed: Error raised for time limit.\nMessage: " + e.getMessage() + Utils.RESET);
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
            statement.executeUpdate("DELETE FROM Workstation_Type_Operation_Type WHERE Workstation_Type_ID = 'WT1'");
            System.out.println(Utils.GREEN + "Cleaned up Workstation_Type_Operation_Type." + Utils.RESET);

            statement.executeUpdate("DELETE FROM Workstation_Type WHERE Workstation_Type_ID = 'WT1'");
            System.out.println(Utils.GREEN + "Cleaned up Workstation_Type." + Utils.RESET);

            statement.executeUpdate("DELETE FROM Operation WHERE Operation_ID IN (1, 2)");
            System.out.println(Utils.GREEN + "Cleaned up Operation." + Utils.RESET);

            statement.executeUpdate("DELETE FROM Operation_Type WHERE Operation_Type_ID = 1");
            System.out.println(Utils.GREEN + "Cleaned up Operation_Type." + Utils.RESET);

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
