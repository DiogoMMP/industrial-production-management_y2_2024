package trees.AVL_BST;

import trees.ProductionTree.NodeType;

import java.util.ArrayList;
import java.util.List;

public class BOO implements Comparable<BOO> {

    private List<String> items;
    private List<Double> quantityItems;
    private String operation;
    private Double quantity;
    private NodeType type;

    /**
     * Creates a new BOO when it is a operation
     * @param operation  item to be added
     */
    public BOO(Double quantity, String operation) {
        this.items = new ArrayList<>();
        this.quantityItems = new ArrayList<>();
        this.operation = operation;
        this.quantity = quantity;
        this.type = NodeType.OPERATION;
    }

    /**
     * Creates a new BOO when it is a material
     * Initially it is empty
     */
    public BOO() {
        this.items = new ArrayList<>();
        this.quantityItems = new ArrayList<>();
        this.operation = "";
        this.quantity = 0.0;
        this.type = NodeType.MATERIAL;
    }


    public String getOperation() {
        return operation;
    }

    public List<String> getItems() {
        return items;
    }

    public Double getQuantity() {
        return quantity;
    }

    @Override
    public int compareTo(BOO o) {
        return this.operation.compareTo(o.operation);
    }

    @Override
    public String toString() {
        return "Items: " + items + " Operation: " + operation + " Quantity: " + quantity;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public void addItems(String item) {
        this.items.add(item);
    }

    public void addQuantity(double quantity) {
        this.quantityItems.add(quantity);
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public void setQuantityItems(List<Double> quantityItems) {
        this.quantityItems = quantityItems;
    }

    public List<Double> getQuantityItems() {
        return quantityItems;
    }

    public int getItemPosition(String item) {
        return items.indexOf(item);
    }
}
