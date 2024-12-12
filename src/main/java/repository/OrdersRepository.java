package repository;

import domain.Order;
import importer_and_exporter.InputFileReader;

import java.util.ArrayList;
import java.util.List;

public class OrdersRepository {
    private List<Order> orders;

    public OrdersRepository() {
        this.orders = new ArrayList<>();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void addOrders(String pathOrd) {
        List<Order> orders = InputFileReader.readOrders(pathOrd);

        try {
            if (orders.isEmpty()){
                throw new Exception("No orders found in the file");
            }
            for (Order order : orders) {
                addOrder(order);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
