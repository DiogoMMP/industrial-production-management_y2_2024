package prodPlanSimulator.UI.Menu;

import prodPlanSimulator.UI.Utils.DataInitializer;

public class StartMenuUI implements Runnable {
    @Override
    public void run() {
        System.out.println("-------------------------------------------");
        System.out.printf("%n");
        System.out.println("Welcome to the Production Plan Simulator!");
        System.out.printf("%n");
        System.out.println("-------------------------------------------");
        boolean success = false;
        while (!success){
            try {
                DataInitializer dataInitializer = new DataInitializer();
                dataInitializer.run();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

    }
}
