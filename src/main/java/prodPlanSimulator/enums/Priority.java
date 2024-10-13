package prodPlanSimulator.enums;

public enum Priority {
    HIGH,
    NORMAL,
    LOW;

    public static Priority fromString(String priority) {
        if (priority != null) {
            for (Priority p : Priority.values()) {
                if (priority.equalsIgnoreCase(p.toString())) {
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
