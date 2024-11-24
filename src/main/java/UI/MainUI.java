package UI;

import UI.Menu.StartMenuUI;
import prodPlanSimulator.repository.OracleDataExporter;

public class MainUI {
    public static void main(String[] args) {
        try {
            OracleDataExporter oracleDataExporter = new OracleDataExporter();
            oracleDataExporter.run();
            StartMenuUI startMenuUI = new StartMenuUI();
            startMenuUI.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
