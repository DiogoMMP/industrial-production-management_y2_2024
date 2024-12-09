package trees.ProductionTree;

public enum NodeType {
    PRODUCT,
    COMPONENT,
    RAW_MATERIAL,
    OPERATION;

    /**
     * Convert a NodeType to a string
     * @return the string representation of the NodeType
     */
    @Override
    public String toString() {
        if (this == PRODUCT) {
            return "Product";
        } else if (this == COMPONENT) {
            return "Component";
        } else if (this == RAW_MATERIAL) {
            return "Raw Material";
        } else if (this == OPERATION) {
            return "Operation";
        }
        return "";
    }
}