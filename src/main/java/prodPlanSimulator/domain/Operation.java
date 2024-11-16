package prodPlanSimulator.domain;

public class Operation {
    private String id;
    private String description;
    private String quantity;

    /**
     * Constructor for Operation
     * @param id Operation ID
     * @param description Operation description
     * @param quantity Operation quantity
     */
    public Operation(String id, String description, String quantity) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
    }

    /**
     * Default constructor for Operation
     */
    public Operation() {
        this.id = "";
        this.description = "";
        this.quantity = "0";
    }

    /**
     * Get the ID of the operation
     * @return Operation ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the description of the operation
     * @return Operation description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the quantity of the operation
     * @return Operation quantity
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Set the ID of the operation
     * @param id Operation ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the description of the operation
     * @param description Operation description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the quantity of the operation
     * @param quantity Operation quantity
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * Override the toString method to return the description of the operation
     * @return Operation description
     */
    @Override
    public String toString() {
        return description;
    }
}
