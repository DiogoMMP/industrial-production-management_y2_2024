package prodPlanSimulator.UI;

import prodPlanSimulator.UI.Menu.MainMenuUI;

public class MainUI {
    public static void main(String[] args) {
        try {
            MainMenuUI menu = new MainMenuUI();
            menu.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
