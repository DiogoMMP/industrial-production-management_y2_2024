package trees.AVL_BST;

import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Operation;

public class BOMBOO implements Comparable<BOMBOO> {

    private Item item;
    private Operation operation;
    private int quantity;
    /**
     * Creates a new BOMBOO when it is a component and not a raw material
     * @param item item to be added
     * @param operation operation to be added
     */
    public BOMBOO(Item item, Operation operation) {
        this.item = item;
        this.operation = operation;
        this.quantity = 1;
    }

    /**
     * Creates a new BOMBOO when it is a raw material
     * @param item  item to be added
     */
    public BOMBOO(Item item) {
        this.item = item;
        this.operation = new Operation();
        this.quantity = 1;
    }


    public Item getItem() {
        return item;
    }

    public Operation getOperation() {
        return operation;
    }

    public int getQuantity() {
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
        return item.getId() + " - " + operation.getDescription();
    }
}
