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
        String operationName = "";
        Simulator simulator = Instances.getInstance().getSimulator();
        LinkedHashMap<LinkedHashMap<Order, String>, List<LinkedHashMap<String, Double>>> orders = simulator.simulateOrders();
        System.out.println("\n\n\033[1m\033[36m--- Simulate Orders ------------\033[0m");
        if (choice.equals("All")) {
            for (LinkedHashMap<Order, String> order : orders.keySet()) {
                Order currentOrder = order.keySet().iterator().next();
                System.out.println("\n\n\033[1m\033[36m--- Order: " + currentOrder.getId() + " Priority: " + currentOrder.getPriority() + " ------------\033[0m");
                List<LinkedHashMap<String, Double>> operationsList = orders.get(order);
                int index = 0;
                for (String itemID : currentOrder.getItemsIdList()) {
                    System.out.println("\n\n\033[1m\033[36mItem ID: " + itemID + " \033[0m");
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
                            System.out.println("\n\n\033[1m\033[36m" + operationOutput + "\033[0m");
                        }
                    }
                    index++;
                }
            }
        } else {
            for (LinkedHashMap<Order, String> order : orders.keySet()) {
                Order currentOrder = order.keySet().iterator().next();
                if (currentOrder.getId() == Integer.parseInt(choice.split(" ")[1])) {
                    System.out.println("\n\n\033[1m\033[36m--- Order: " + currentOrder.getId() + " Priority: " + currentOrder.getPriority() + " ------------\033[0m");
                    List<LinkedHashMap<String, Double>> operationsList = orders.get(order);
                    int index = 0;
                    for (String itemID : currentOrder.getItemsIdList()) {
                        System.out.println("\n\n\033[1m\033[36mItem ID: " + itemID + " \033[0m");
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
                                System.out.println("\n\n\033[1m\033[36m" + operationOutput + "\033[0m");
                            }
                        }
                        index++;
                    }
                }
            }
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
