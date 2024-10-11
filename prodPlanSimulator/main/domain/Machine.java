package main.domain;


public class Machine {
    private String id;
    private String operation;
    private int time;
    private Item item;

    /**
     * Machine Builder
     * @param id Machine ID
     * @param operation Machine operation
     * @param time Machine time
     */
    public Machine(String id, String operation, int time) {
        this.id = id;
        this.operation = operation;
        this.time = time;
    }

    /**
     * Empty Machine Builder
     */
    public Machine() {
        this.id = "";
        this.operation = "";
        this.time = 0;
    }

    // Getters e Setters

    /**
     * Gets the ID of the machine
     * @return ID of the machine
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the machine
     * @param id new ID of the machine
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the operation of the machine
     * @return operation of the machine
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Sets the operation of the machine
     * @param operation new operation of the machine
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * Gets the time of the machine
     * @return time of the machine
     */
    public int getTime() {
        return time;
    }

    /**
     * Sets the time of the machine
     * @param time new time of the machine
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Gets the item of the machine
     * @return item of the machine
     */

    public Item getItem() {
        return item;
    }

    /**
     * Sets the item of the machine
     * @param item new item of the machine
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Clear the item of the machine
     */
    public void clearUpMachine() {
        this.item = null;
    }
}
