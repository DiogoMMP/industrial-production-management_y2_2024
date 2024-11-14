package prodPlanSimulator.domain;

public class Material {
    private String ID;
    private String name;
    private String description;
    private String itemType;
    private String quantity;

    // Construtor
    public Material(String ID, String name, String description, String itemType, String quantity) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.itemType = itemType;
        this.quantity = quantity;
    }

    // Getters e Setters
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public String toString() {

        if (description.equals("Unknown description")) {
            return quantity + "x " + name + " (Material)";
        }
        return quantity + "x " + name + " (" + description + ") (Material)";
    }
}