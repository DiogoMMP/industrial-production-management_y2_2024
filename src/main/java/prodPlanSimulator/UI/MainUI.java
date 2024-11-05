package prodPlanSimulator.UI;

import prodPlanSimulator.UI.Utils.DataInitializer;

public class MainUI {
    public static void main(String[] args) {
        try {
            DataInitializer dataInitializer = new DataInitializer();
            dataInitializer.run();
            ChooseSimulatorUI chooseSimulatorUI = new ChooseSimulatorUI();
            chooseSimulatorUI.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
