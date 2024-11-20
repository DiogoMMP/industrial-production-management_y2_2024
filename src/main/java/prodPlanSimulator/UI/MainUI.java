package prodPlanSimulator.UI;

import prodPlanSimulator.UI.Menu.StartMenuUI;
import prodPlanSimulator.UI.Simulators.ChooseSimulatorUI;
import prodPlanSimulator.UI.Utils.DataInitializer;
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
