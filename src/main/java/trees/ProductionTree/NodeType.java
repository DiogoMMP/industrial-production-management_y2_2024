package trees.ProductionTree;

public enum NodeType {
    MATERIAL,
    OPERATION;

    /**
     * Convert a NodeType to a string
     * @return the string representation of the NodeType
     */
    @Override
    public String toString() {
        if (this == MATERIAL) {
            return "Material";
        } else if (this == OPERATION) {
            return "Operation";
        }
        return "";
    }
}