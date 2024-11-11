package prodPlanSimulator.domain;

public class Operation implements Comparable<Operation> {
    /**
     * Operation ID
     */
    private int id;
    /**
     * Operation description
     */
    private String description;

    /**
     * Operation Builder
     * @param id Operation ID
     * @param description Operation description
     */
    public Operation(int id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Operation Builder
     */
    public Operation() {
        this.id = 0;
        this.description = "";
    }

    /**
     * Gets the id of the operation
     * @return id of the operation
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the operation
     * @param id new id of the operation
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the description of the operation
     * @return description of the operation
     */

    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the operation
     * @param description new description of the operation
     */

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Operation toString
     * @return Operation description
     */
    @Override
    public int compareTo(Operation o) {
        return Integer.compare(this.id, o.id);
    }
}
