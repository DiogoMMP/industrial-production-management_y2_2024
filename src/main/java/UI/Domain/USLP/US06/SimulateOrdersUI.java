package UI.Domain.USLP.US06;

import UI.Menu.MainMenuOrders;
import UI.Menu.MenuItem;
import UI.Utils.Utils;
import domain.Order;
import prodPlanSimulator.Simulator;
import repository.Instances;
import repository.OrdersRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SimulateOrdersUI implements Runnable {
    Simulator simulator = Instances.getInstance().getSimulator();
    LinkedHashMap<LinkedHashMap<Order, String>, List<LinkedHashMap<String, Double>>> orders = simulator.simulateOrders();
    @Override
    public void run() {
        String choice;
        List<Order> orderList = new ArrayList<>();
        for (LinkedHashMap<Order, String> order : orders.keySet()) {
            orderList.add(order.keySet().iterator().next());
        }
        List<MenuItem> options = new ArrayList<>();
        for (Order order : orderList) {
            options.add(new MenuItem("Order: " + order.getId(), new SimulateOrdersUI()));
        }
        options.add(new MenuItem("All", new SimulateOrdersUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n\033[1m\033[36m--- Choose the Order to be Visualized ------------\033[0m");
            if ((option >= 0) && (option < options.size())) {
                choice = options.get(option).toString();
                if (!choice.equals("Back")) {
                    show(choice);
                    Utils.goBackAndWait();
                }
            }
        } while (option != -1 && !options.get(option).toString().equals("Back"));
    }

    private void show(String choice) {
        OrdersRepository ordersRepository = Instances.getInstance().getOrdersRepository();
        System.out.println("\n\n\033[1m\033[36m--- Simulate Orders ------------\033[0m");
        if (choice.equals("All")) {
            for (LinkedHashMap<Order, String> order : orders.keySet()) {
                System.out.println("\n\n\033[1m\033[36m--- Order: " + order.keySet().iterator().next().getId() + " Priority: " + order.keySet().iterator().next().getPriority() + " ------------\033[0m");
                for (LinkedHashMap<String, Double> operation : orders.get(order)) {
                    System.out.println("\n\n\033[1m\033[36m" + operation.keySet().iterator().next() + " \033[0m");
                }
            }
        } else {
            for (LinkedHashMap<Order, String> order : orders.keySet()) {
                if (order.keySet().iterator().next().getId() == Integer.parseInt(choice.split(" ")[1])) {
                    System.out.println("\n\n\033[1m\033[36m--- Order: " + order.keySet().iterator().next().getId() + " Priority: " + order.keySet().iterator().next().getPriority() + " ------------\033[0m");
                    for (LinkedHashMap<String, Double> operation : orders.get(order)) {
                        System.out.println("\n\n\033[1m\033[36m" + operation.keySet().iterator().next() + " \033[0m");
                    }
                }
            }
        }
    }

}
