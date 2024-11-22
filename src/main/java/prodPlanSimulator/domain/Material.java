package prodPlanSimulator.domain;

public class Material implements Comparable<Material> {
    private String ID;
    private String name;
    private String quantity;

    /**
     * Constructor for Material
     * @param ID Material ID
     * @param name Material name
     * @param quantity Material quantity
     */
    public Material(String ID, String name, String quantity) {
        this.ID = ID;
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Get the ID of the material
     * @return Material ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Set the ID of the material
     * @param ID Material ID
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Get the name of the material
     * @return Material name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the material
     * @param name Material name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the quantity of the material
     * @return Material quantity
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Set the quantity of the material
     * @param quantity Material quantity
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * Override the toString method to return the quantity and name of the material
     * @return Material quantity and name
     */
    @Override
    public String toString() {
        return quantity + "x " + name;
    }

    @Override
    public int compareTo(Material o) {
        return this.getID().compareTo(o.getID());
    }
}