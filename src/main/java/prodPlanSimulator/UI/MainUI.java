package prodPlanSimulator.UI;

import prodPlanSimulator.UI.Menu.MainMenuUI;
import prodPlanSimulator.UI.Utils.DataInitializer;

public class MainUI {
    public static void main(String[] args) {
        try {
            DataInitializer dataInitializer = new DataInitializer();
            dataInitializer.run();
            MainMenuUI menu = new MainMenuUI();
            menu.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
