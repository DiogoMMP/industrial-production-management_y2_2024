package domain;

import enums.Priority;

import java.util.List;

public class Order {
    private List<String> itemsIdList;
    private int id;
    private Priority priority;
    private List<Integer> quantity;

    public Order(List<String> itemsIdList, int id, Priority priority, List<Integer> quantity) {
        this.itemsIdList = itemsIdList;
        this.id = id;
        this.priority = priority;
        this.quantity = quantity;
    }

    public List<String> getItemsIdList() {
        return itemsIdList;
    }

    public void setItemsIdList(List<String> itemsIdList) {
        this.itemsIdList = itemsIdList;
    }

    public int getId() {
        return id;
    }

    public String getIdString() {
        return String.valueOf(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public List<Integer> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<Integer> quantity) {
        this.quantity = quantity;
    }
    public void addItemsId(String itemId) {
        itemsIdList.add(itemId);
    }


}
