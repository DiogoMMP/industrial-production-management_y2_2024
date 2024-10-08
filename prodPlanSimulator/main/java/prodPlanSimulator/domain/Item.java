package prodPlanSimulator.domain;

import prodPlanSimulator.enums.Priority;

import java.util.List;

public class Item {
    private int id;
    private Priority priority;
    private List<String> operations;
    private int currentOperationIndex;

    /**
     * Item Builder
     * @param id Item ID
     * @param priority Item priority
     * @param operations Item operations
     */
    public Item(int id, Priority priority, List<String> operations) {
        this.id = id;
        this.priority = priority;
        this.operations = operations;
        this.currentOperationIndex = 0;
    }

    /**
     * Empty Item Builder
     */
    public Item(){
        this.id = 0;
        this.priority = null;
        this.operations = null;
        this.currentOperationIndex = 0;
    }

    // Getters e Setters

    /**
     * Gets the ID of the product
     * @return ID of the product
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the product
     * @param id new ID of the product
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the priority of the item
     * @return priority of the item
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the item
     * @param priority new priority of the item
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Gets the operations of the item
     * @return operations of the item
     */
    public List<String> getOperations() {
        return operations;
    }

    /**
     * Sets the operations of the item
     * @param operations new operations of the item
     */
    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    /**
     * Gets the current operation index of the item
     * @return current operation index of the item
     */
    public int getCurrentOperationIndex() {
        return currentOperationIndex;
    }

    /**
     * Sets the current operation index of the item
     * @param currentOperationIndex new current operation index of the item
     */
    public void setCurrentOperationIndex(int currentOperationIndex) {
        this.currentOperationIndex = currentOperationIndex;
    }
}

