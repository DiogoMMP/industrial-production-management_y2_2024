package prodPlanSimulator.UI.Menu;

import prodPlanSimulator.UI.Utils.DataInitializer;

public class StartMenuUI implements Runnable {
    @Override
    public void run() {
        System.out.println("-----------------------------");
        System.out.printf("%n");
        System.out.println("Welcome to the Production Plan Simulator!");
        System.out.printf("%n");
        System.out.println("-----------------------------");
        DataInitializer dataInitializer = new DataInitializer();
        dataInitializer.run();
    }
}
