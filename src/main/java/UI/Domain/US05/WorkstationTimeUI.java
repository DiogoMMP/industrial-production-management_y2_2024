package UI.Domain.US05;

import UI.Utils.Utils;
import domain.Item;

public class WorkstationTimeUI implements Runnable {

    @Override
    public void run() {
        clearConsole();
        show();
        Utils.goBackAndWait();
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void show() {
        System.out.println("\n\n\033[1m\033[36m--- Workstations by Ascending Order ------------\033[0m");
        Item.listWorkstationsByAscOrder();
    }
}