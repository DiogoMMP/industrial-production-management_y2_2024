package trees.AVL_BST;

import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Operation;
import trees.ProductionTree.NodeType;

public class BOMBOO implements Comparable<BOMBOO> {

    private String item;
    private String operation;
    private Double quantity;
    private NodeType type;
    /**
     * Creates a new BOMBOO when it is a material
     * @param item item to be added
     */
    public BOMBOO(String item, Double quantity) {
        this.item = item;
        this.operation = "";
        this.quantity = quantity;
        this.type = NodeType.MATERIAL;
    }

    /**
     * Creates a new BOMBOO when it is a operation
     * @param operation  item to be added
     */
    public BOMBOO(Double quantity, String operation) {
        this.item = "";
        this.operation = operation;
        this.quantity = quantity;
        this.type = NodeType.OPERATION;
    }

    public BOMBOO(String item) {
        this.item = item;
        this.operation = "";
        this.quantity = 0.0;
        this.type = NodeType.OPERATION;
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

    @Override
    public int compareTo(BOMBOO o) {
        return item.compareTo(o.getItem());
    }

    @Override
    public String toString() {
        return "Item: " + item + " Operation: " + operation + " Quantity: " + quantity;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }
}
