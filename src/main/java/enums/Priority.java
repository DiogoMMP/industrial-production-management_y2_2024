package enums;

public enum Priority {
    HIGH,
    NORMAL,
    LOW;

    /**
     * Convert a string to a Priority
     * @param priority the string to convert
     * @return the Priority or null if the string does not match any Priority
     */
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

    /**
     * Convert a Priority to a string
     * @return the string representation of the Priority
     */
    @Override
    public String toString() {
        if (this == HIGH) {
            return "High";
        } else if (this == NORMAL) {
            return "Normal";
        } else if (this == LOW) {
            return "Low";
        }
        return null;
    }
}
