package UI.Domain.USLP.US07;

import domain.Item;
import domain.Operation;
import domain.Workstation;
import enums.Priority;
import repository.HashMap_Items_Machines;
import repository.Instances;

import java.sql.*;
import java.util.*;

public class ProductionDataLoader {
    private final Connection connection;

    public ProductionDataLoader(String dbUrl, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(dbUrl, username, password);
    }

    /**
     * Loads all production data for a specific product
     * @param productId The ID of the product to load data for
     * @return Item object representing the main product with all its dependencies
     */
    public Item loadProductData(String productId) throws SQLException {
        // First load operations for the product
        Map<Integer, Operation> operations = loadOperationsForProduct(productId);

        // Then load the product with its operations
        return loadProduct(productId, operations);
    }

    private Map<Integer, Operation> loadOperationsForProduct(String productId) throws SQLException {
        Map<Integer, Operation> operations = new HashMap<>();
        String query =
                "WITH OperationSequence (Operation_ID, Next_Operation_ID, Expected_Estimated_Time, " +
                        "                       Operation_Description, Sequence_Order) AS ( " +
                        "    SELECT b.Operation_ID, b.Next_Operation_ID, " +
                        "           o.Expected_Estimated_Time, ot.Operation_Description, " +
                        "           1 as Sequence_Order " +
                        "    FROM BOO b " +
                        "    JOIN Operation o ON b.Operation_ID = o.Operation_ID " +
                        "    JOIN Operation_Type ot ON o.Operation_Type_ID = ot.Operation_Type_ID " +
                        "    WHERE b.Product_ID = ? " +
                        "      AND NOT EXISTS (SELECT 1 FROM BOO b2 " +
                        "                     WHERE b2.Product_ID = b.Product_ID " +
                        "                       AND b2.Next_Operation_ID = b.Operation_ID) " +
                        "    UNION ALL " +
                        "    SELECT b.Operation_ID, b.Next_Operation_ID, " +
                        "           o.Expected_Estimated_Time, ot.Operation_Description, " +
                        "           os.Sequence_Order + 1 " +
                        "    FROM BOO b " +
                        "    JOIN Operation o ON b.Operation_ID = o.Operation_ID " +
                        "    JOIN Operation_Type ot ON o.Operation_Type_ID = ot.Operation_Type_ID " +
                        "    JOIN OperationSequence os ON b.Operation_ID = os.Next_Operation_ID " +
                        "    WHERE b.Product_ID = ? " +
                        ") " +
                        "SELECT Operation_ID, Operation_Description, Expected_Estimated_Time, Sequence_Order " +
                        "FROM OperationSequence " +
                        "ORDER BY Sequence_Order";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, productId);
            pstmt.setString(2, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Operation operation = new Operation(
                            rs.getString("Operation_ID"),
                            rs.getString("Operation_Description"),
                            rs.getDouble("Expected_Estimated_Time")
                    );
                    operations.put(rs.getInt("Sequence_Order"), operation);
                }
            }
        }
        return operations;
    }

    private Item loadProduct(String productId, Map<Integer, Operation> operations) throws SQLException {
        String query =
                "SELECT p.Product_ID, p.Product_Name " +
                        "FROM Product p " +
                        "WHERE p.Product_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    List<Operation> orderedOperations = new ArrayList<>(operations.values());
                    return new Item(
                            rs.getString("Product_ID"),
                            Priority.NORMAL,
                            orderedOperations,
                            new ArrayList<>() // Empty list for required items if not needed
                    );
                }
            }
        }
        return null;
    }

    public List<Workstation> loadWorkstations() throws SQLException {
        List<Workstation> workstations = new ArrayList<>();
        String query =
                "SELECT w.Workstation_ID, w.Workstation_Setup_Time, w.Workstation_Time, " +
                        "       ot.Operation_Type_ID, ot.Operation_Description, " +
                        "       wto.Maximum_Execution_Time " +
                        "FROM Workstation w " +
                        "JOIN Workstation_Type wt ON w.Workstation_Type_ID = wt.Workstation_Type_ID " +
                        "JOIN Workstation_Type_Operation_Type wto ON wt.Workstation_Type_ID = wto.Workstation_Type_ID " +
                        "JOIN Operation_Type ot ON wto.Operation_Type_ID = ot.Operation_Type_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Operation operation = new Operation(
                        rs.getString("Operation_Type_ID"),
                        rs.getString("Operation_Description"),
                        rs.getDouble("Maximum_Execution_Time")
                );

                Workstation workstation = new Workstation(
                        rs.getString("Workstation_ID"),
                        operation,
                        rs.getInt("Workstation_Setup_Time")
                );
                workstations.add(workstation);
            }
        }
        return workstations;
    }
    /**
     * Initialize the production system with loaded data
     */
    public void initializeProductionSystem(String productId) throws SQLException {
        // Load workstations
        List<Workstation> workstations = loadWorkstations();

        // Load main product with all dependencies
        Item mainProduct = loadProductData(productId);

        // Create HashMap_Items_Machines
        HashMap_Items_Machines hashMap = new HashMap_Items_Machines();

        // Add workstations and items to the hash map
        for (Workstation workstation : workstations) {
            for (Item item : getAllItems(mainProduct)) {
                if (canProcess(workstation, item)) {
                    hashMap.addItemWorkstation(item, workstation);
                }
            }
        }

        // Set the hash map in Instances
        Instances.getInstance().setHashMapItemsWorkstations(hashMap);
    }

    /**
     * Helper method to get all items including dependencies
     */
    private List<Item> getAllItems(Item mainProduct) {
        List<Item> allItems = new ArrayList<>();
        collectItems(mainProduct, allItems);
        return allItems;
    }

    private void collectItems(Item item, List<Item> allItems) {
        if (!allItems.contains(item)) {
            allItems.add(item);
            for (Item requiredItem : item.getItemsRequired()) {
                collectItems(requiredItem, allItems);
            }
        }
    }

    /**
     * Check if a workstation can process an item
     */
    private boolean canProcess(Workstation workstation, Item item) {
        if (item.getOperationsRequired().isEmpty()) {
            return false;
        }

        Operation itemOperation = item.getOperationsRequired().get(item.getCurrentOperationIndex());
        return workstation.getOperationName().equals(itemOperation.getDescription());
    }
}