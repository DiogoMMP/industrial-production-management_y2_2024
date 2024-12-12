package UI.Simulators;

import UI.Menu.MainMenuOrders;

public class ProcessByOrdersUI implements Runnable {
    @Override
    public void run() {
        MainMenuOrders mainMenuOrders = new MainMenuOrders();
        mainMenuOrders.run();
    }
}
