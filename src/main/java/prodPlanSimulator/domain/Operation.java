package prodPlanSimulator.domain;

public class Operation {
    private String id;
    private String description;
    private int order;

    public Operation(String id, String description, int order) {
        this.id = id;
        this.description = description;
        this.order = order;
    }

    public Operation() {
        this.id = "";
        this.description = "";
        this.order = 0;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return description + " (Operation)";
    }
}
