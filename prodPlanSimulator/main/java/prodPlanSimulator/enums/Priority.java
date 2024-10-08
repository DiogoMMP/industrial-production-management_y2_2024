package prodPlanSimulator.enums;

public enum Priority {
    HIGH,
    MEDIUM,
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

    public static String toString(Priority priority) {
        return priority.toString();
    }


}
