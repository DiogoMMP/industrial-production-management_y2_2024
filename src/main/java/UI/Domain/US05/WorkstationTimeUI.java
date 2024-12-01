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
        System.out.println("\n\n--- Workstations by Ascending Order ------------");
        Item.listWorkstationsByAscOrder();
    }
}