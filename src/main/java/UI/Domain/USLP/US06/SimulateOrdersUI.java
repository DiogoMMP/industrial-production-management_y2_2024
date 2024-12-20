package UI.Domain.USLP.US06;

import UI.Menu.MenuItem;
import UI.Menu.OrdersMenu;
import UI.Simulators.ChooseSimulatorUI;
import UI.Utils.Utils;
import domain.Order;
import enums.Priority;
import prodPlanSimulator.Simulator;
import repository.Instances;
import repository.OrdersRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SimulateOrdersUI implements Runnable {

    @Override
    public void run() {
        OrdersRepository ordersRepository = Instances.getInstance().getOrdersRepository();
        String choice;
        List<Order> orderList = ordersRepository.getOrders();
        List<MenuItem> options = new ArrayList<>();

        for (Order order : orderList) {
            options.add(new MenuItem("Order: " + order.getId(), new SimulateOrdersUI()));
        }
        options.add(new MenuItem("All", new SimulateOrdersUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n" + Utils.BOLD + Utils.CYAN +
                    "--- Choose the Order to be Visualized ------------" + Utils.RESET);

            if (option == -2) {
                new OrdersMenu().run();
            }

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
        Simulator simulator = Instances.getInstance().getSimulator();
        LinkedHashMap<LinkedHashMap<Order, String>, List<LinkedHashMap<String, Double>>> orders = simulator.simulateOrders();

        System.out.println("\n\n" + Utils.BOLD + Utils.CYAN + "--- Simulate Orders ------------" + Utils.RESET);

        if (choice.equals("All")) {
            for (LinkedHashMap<Order, String> order : orders.keySet()) {
                Order currentOrder = order.keySet().iterator().next();

                ordersTitle(orders, order, currentOrder);
            }
        } else {
            for (LinkedHashMap<Order, String> order : orders.keySet()) {
                Order currentOrder = order.keySet().iterator().next();

                if (currentOrder.getId() == Integer.parseInt(choice.split(" ")[1])) {

                    ordersTitle(orders, order, currentOrder);
                }
            }
        }
    }

    private void ordersTitle(LinkedHashMap<LinkedHashMap<Order, String>, List<LinkedHashMap<String, Double>>> orders, LinkedHashMap<Order, String> order, Order currentOrder) {
        String operationName;
        if (currentOrder.getPriority() == Priority.HIGH){
            System.out.println("\n\n" + Utils.BOLD + Utils.RED + "--- Order: " + currentOrder.getId() +
                    " Priority: " + currentOrder.getPriority() + " ------------" + Utils.RESET); // Red

        } else if (currentOrder.getPriority() == Priority.NORMAL) {
            System.out.println("\n\n" + Utils.BOLD + Utils.YELLOW + "--- Order: " + currentOrder.getId() +
                    " Priority: " + currentOrder.getPriority() + " ------------" + Utils.RESET); // Yellow

        } else {
            System.out.println("\n\n" + Utils.BOLD + Utils.GREEN + "--- Order: " + currentOrder.getId() +
                    " Priority: " + currentOrder.getPriority() + " ------------" + Utils.RESET); // Green
        }

        List<LinkedHashMap<String, Double>> operationsList = orders.get(order);
        int index = 0;

        for (String itemID : currentOrder.getItemsIdList()) {
            System.out.println("\n" + Utils.BOLD + "Item ID: " + itemID + Utils.RESET);

            if (index < operationsList.size()) {
                LinkedHashMap<String, Double> operation = operationsList.get(index);

                for (Map.Entry<String, Double> entry : operation.entrySet()) {
                    operationName = extractOperationName(entry.getKey());

                    if (!operationName.equalsIgnoreCase(extractOperationName(entry.getKey()))) {
                        operationName = extractOperationName(entry.getKey());
                        System.out.printf("%n");
                        System.out.println("Operation: " + operationName);
                    }

                    String operationOutput = entry.getKey().replaceAll(" - Operation: " + operationName, "");
                    System.out.println(operationOutput);
                }
            }
            index++;
        }
    }

    private String extractOperationName(String key) {
        // Assuming the operation name is the part after "Operation: " and before the next " - "
        int startIndex = key.indexOf("Operation: ") + 11;
        int endIndex = key.indexOf(" - ", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return key.substring(startIndex, endIndex).trim();
        }
        return "Unknown";
    }

}
