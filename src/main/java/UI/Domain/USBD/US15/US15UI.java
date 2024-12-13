package UI.Domain.USBD.US15;

import importer_and_exporter.OracleDataExporter;

import java.sql.*;
import java.util.Scanner;

public class US15UI implements Runnable {
    @Override
    public void run() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Query to retrieve existing workstations and their attributes
            String query = "SELECT Workstation_ID, Workstation_Name, Workstation_Description, Workstation_Setup_Time, " +
                    "Workstation_Time, Plant_Floor_ID, Workstation_Type_ID, Min_Temp, Max_Temp, Min_Hum, Max_Hum FROM Workstation";
            ResultSet resultSet = statement.executeQuery(query);

            // Print the results
            System.out.println("Existing Workstations:");
            while (resultSet.next()) {
                int workstationId = resultSet.getInt("Workstation_ID");
                String workstationName = resultSet.getString("Workstation_Name");
                String workstationDescription = resultSet.getString("Workstation_Description");
                double workstationSetupTime = resultSet.getDouble("Workstation_Setup_Time");
                double workstationTime = resultSet.getDouble("Workstation_Time");
                int plantFloorId = resultSet.getInt("Plant_Floor_ID");
                String workstationTypeId = resultSet.getString("Workstation_Type_ID");
                double minTemp = resultSet.getDouble("Min_Temp");
                double maxTemp = resultSet.getDouble("Max_Temp");
                double minHum = resultSet.getDouble("Min_Hum");
                double maxHum = resultSet.getDouble("Max_Hum");

                System.out.printf("Workstation ID: %d, Name: %s, Description: %s, Setup Time: %.2f, Time: %.2f, Plant Floor ID: %d, Type ID: %s, Min Temp: %.2f, Max Temp: %.2f, Min Hum: %.2f, Max Hum: %.2f%n",
                        workstationId, workstationName, workstationDescription, workstationSetupTime, workstationTime, plantFloorId, workstationTypeId, minTemp, maxTemp, minHum, maxHum);
            }

            // Prompt the user for input
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter Workstation ID: ");
            int newWorkstationId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Workstation Name: ");
            String newWorkstationName = scanner.nextLine();

            System.out.print("Enter Workstation Description: ");
            String newWorkstationDescription = scanner.nextLine();

            System.out.print("Enter Workstation Setup Time: ");
            double newWorkstationSetupTime = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Workstation Time: ");
            double newWorkstationTime = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Plant Floor ID: ");
            int newPlantFloorId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Workstation Type ID: ");
            String newWorkstationTypeId = scanner.nextLine();

            System.out.print("Enter Min Temp: ");
            double minTemp = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Max Temp: ");
            double maxTemp = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Min Humidity: ");
            double minHum = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Max Humidity: ");
            double maxHum = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            // Call the Register_Workstation function
            String resultMessage = registerWorkstation(connection, newWorkstationId, newWorkstationName, newWorkstationDescription, newWorkstationSetupTime,
                    newWorkstationTime, newPlantFloorId, newWorkstationTypeId, minTemp, maxTemp, minHum, maxHum);
            System.out.println("Function Result: " + resultMessage);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(OracleDataExporter.DB_URL, OracleDataExporter.USER, OracleDataExporter.PASS);
    }

    private String registerWorkstation(Connection connection, int workstationId, String workstationName, String workstationDescription, double workstationSetupTime,
                                       double workstationTime, int plantFloorId, String workstationTypeId, double minTemp, double maxTemp, double minHum, double maxHum) throws SQLException {
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
            return callableStatement.getString(1);
        }
    }
}
