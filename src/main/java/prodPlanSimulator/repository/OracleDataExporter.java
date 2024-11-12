package prodPlanSimulator.repository;

import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;

public class OracleDataExporter {

    private static final String DB_URL = "jdbc:odbc:YourDSNName";
    private static final String USER = "yourUsername";
    private static final String PASS = "yourPassword";

    public static void main(String[] args) {
        OracleDataExporter exporter = new OracleDataExporter();
        exporter.exportBOOToCSV("outputBOO.csv");
        exporter.exportBOMToCSV("outputBOM.csv");
    }

    public void exportBOOToCSV(String fileName) {
        String query = "SELECT * FROM BOO WHERE article_id = ?";
        exportToCSV(query, fileName);
    }

    public void exportBOMToCSV(String fileName) {
        String query = "SELECT * FROM BOM WHERE article_id = ?";
        exportToCSV(query, fileName);
    }

    private void exportToCSV(String query, String fileName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query);
             FileWriter csvWriter = new FileWriter(fileName)) {

            pstmt.setString(1, "article_id_value"); // ajustar o ID do artigo específico
            ResultSet rs = pstmt.executeQuery();

            // Obter metadados para extrair nomes das colunas
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Escrever cabeçalhos no CSV
            for (int i = 1; i <= columnCount; i++) {
                csvWriter.append(metaData.getColumnName(i));
                if (i < columnCount) csvWriter.append(",");
            }
            csvWriter.append("\n");

            // Escrever dados no CSV
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(rs.getString(i));
                    if (i < columnCount) csvWriter.append(",");
                }
                csvWriter.append("\n");
            }

            System.out.println("Data exported to " + fileName);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
