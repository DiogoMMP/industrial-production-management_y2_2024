package prodPlanSimulator.domain;


import java.util.List;

public class Machine {
    private String id;
    private List<String> operations;
    private int time;
    private Boolean hasItem = false;

    /**
     * Machine Builder
     * @param id Machine ID
     * @param operations Machine operation
     * @param time Machine time
     */
    public Machine(String id, List<String> operations, int time) {
        this.id = id;
        this.operations = operations;
        this.time = time;
    }

    /**
     * Empty Machine Builder
     */
    public Machine() {
        this.id = "";
        this.operations = null;
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
     *
     * @return operation of the machine
     */
    public List<String> getOperations() {
        return operations;
    }

    /**
     * Sets the operation of the machine
     * @param operations new operation of the machine
     */
    public void setOperations(List<String> operations) {
        this.operations = operations;
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

    public Boolean getHasItem() {
        return hasItem;
    }

    /**
     * Sets the item of the machine
     * @param hasItem new item of the machine
     */
    public void setHasItem(Boolean hasItem) {
        this.hasItem = hasItem;
    }

    /**
     * Clear the item of the machine
     */
    public void clearUpMachine() {
        this.hasItem = false;
    }
}
