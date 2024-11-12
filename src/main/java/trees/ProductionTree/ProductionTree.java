package trees.ProductionTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import prodPlanSimulator.domain.Material;
import prodPlanSimulator.domain.Operation;

public class ProductionTree {
    private TreeNode<String> root;
    private Map<String, TreeNode<String>> nodesMap = new HashMap<>();

    public ProductionTree(String mainObjective) {
        this.root = new TreeNode<>("Build " + mainObjective);
    }

    public TreeNode<String> getRoot() {
        return root;
    }

    /**
     * Build a tree structure from two CSV files: one with Bill of Operations (BOO)
     * and another with Bill of Materials (BOM).
     *
     * @param booFilePath Path to the BOO file
     * @param bomFilePath Path to the BOM file
     */
    public void buildProductionTree(String booFilePath, String bomFilePath) {

        List<String[]> booData = readCsvFile(booFilePath);
        List<String[]> bomData = readCsvFile(bomFilePath);

        Map<String, List<Operation>> productOperations = new HashMap<>();
        Map<String, List<Material>> operationMaterials = new HashMap<>();

        // Primeiro, processamos o BOO para organizar operações por produto
        for (String[] operationData : booData) {
            if (operationData.length < 4) {
                continue;
            }

            String productId = operationData[0];
            String operationId = operationData[1];
            int operationOrder = Integer.parseInt(operationData[2]);
            String operationDescription = operationData[3];

            Operation operation = new Operation(operationId, operationDescription, operationOrder);
            productOperations.computeIfAbsent(productId, k -> new ArrayList<>()).add(operation);
            nodesMap.put(operationId, new TreeNode<>(operationDescription)); // Mapeia operações para criação de subárvores
        }

        // Em seguida, processamos o BOM para associar materiais às operações
        for (String[] materialData : bomData) {
            if (materialData.length < 6) {
                continue;
            }

            String parentId = materialData[0];
            String itemId = materialData[1];
            String itemType = materialData[2];
            String itemName = materialData[3];
            String description = materialData[4];
            int quantity = Integer.parseInt(materialData[5]);

            Material material = new Material(itemId, itemName, description, itemType, quantity);
            operationMaterials.computeIfAbsent(parentId, k -> new ArrayList<>()).add(material);
        }

        // Construir a árvore para cada produto com as operações e materiais
        for (String productId : productOperations.keySet()) {
            TreeNode<String> productNode = new TreeNode<>("Produto " + productId);
            root.addChild(productNode); // Adiciona cada produto como filho do root

            // Adiciona operações ao produto
            List<Operation> operations = productOperations.get(productId);
            if (operations != null) {
                for (Operation operation : operations) {
                    TreeNode<String> operationNode = new TreeNode<>(operation.getDescription());
                    productNode.addChild(operationNode);

                    // Adiciona materiais à operação
                    List<Material> materials = operationMaterials.get(operation.getId());
                    if (materials != null) {
                        for (Material material : materials) {
                            TreeNode<String> materialNode = new TreeNode<>(material.getName() + " (" + material.getDescription() + ") x" + material.getQuantity());
                            operationNode.addChild(materialNode);

                            // Se o material tiver submateriais (raw materials), cria subárvores
                            addRawMaterials(materialNode, material.getID(), operationMaterials);
                        }
                    }
                }
            }
        }
    }

    /**
     * Método auxiliar para adicionar subárvores de materiais (raw materials) recursivamente.
     */
    private void addRawMaterials(TreeNode<String> parent, String materialId, Map<String, List<Material>> operationMaterials) {
        List<Material> rawMaterials = operationMaterials.get(materialId);
        if (rawMaterials != null) {
            for (Material rawMaterial : rawMaterials) {
                TreeNode<String> rawMaterialNode = new TreeNode<>(rawMaterial.getName() + " (" + rawMaterial.getDescription() + ") x" + rawMaterial.getQuantity());
                parent.addChild(rawMaterialNode);

                // Recursivamente adiciona submateriais se houver mais camadas
                addRawMaterials(rawMaterialNode, rawMaterial.getID(), operationMaterials);
            }
        }
    }


    // Método auxiliar para leitura de ficheiros CSV
    private List<String[]> readCsvFile(String filePath) {
        List<String[]> data = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (int i = 1; i < lines.size(); i++) { // Começa a ler da segunda linha para ignorar o cabeçalho
                String[] values = lines.get(i).split(";");
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    public static void main(String[] args) {
        ProductionTree tree = new ProductionTree("Table");
        tree.buildProductionTree("src/main/resources/BOO.csv", "src/main/resources/BOM.csv");
        System.out.println(tree.getRoot().getValue());
        for (TreeNode<String> child : tree.getRoot().getChildren()) {
            System.out.println("  " + child.getValue());
            for (TreeNode<String> grandChild : child.getChildren()) {
                System.out.println("    " + grandChild.getValue());
            }
        }
    }
}
