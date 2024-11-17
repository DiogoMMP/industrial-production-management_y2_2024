package trees.AVL_BST;

import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Operation;

public class BOMBOO implements Comparable<BOMBOO> {

    private String item;
    private String operation;
    private Double quantity;
    /**
     * Creates a new BOMBOO when it is a component and not a raw material
     * @param item item to be added
     * @param operation operation to be added
     */
    public BOMBOO(String item, String operation, Double quantity) {
        this.item = item;
        this.operation = operation;
        this.quantity = quantity;
    }

    /**
     * Creates a new BOMBOO when it is a raw material
     * @param item  item to be added
     */
    public BOMBOO(String item, Double quantity) {
        this.item = item;
        this.operation = "";
        this.quantity = quantity;
    }



    public String getOperation() {
        return operation;
    }

    public String getItem() {
        return item;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void incQuantity(){
        quantity++;
    }

    @Override
    public int compareTo(BOMBOO o) {
        return item.compareTo(o.getItem());
    }

    @Override
    public String toString() {
        return "Item: " + item + " Operation: " + operation + " Quantity: " + quantity;
    }
}
