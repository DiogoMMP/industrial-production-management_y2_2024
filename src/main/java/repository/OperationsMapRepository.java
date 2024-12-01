package repository;

import importer_and_exporter.InputFileReader;

import java.util.HashMap;
import java.util.Map;

public class OperationsMapRepository {
    Map<String, String> operations;

    public OperationsMapRepository(Map<String, String> items) {
        this.operations = items;
    }

    public OperationsMapRepository() {
        this.operations = new HashMap<>();
    }

    public Map<String, String> getOperationsMapRepository() {
        return operations;
    }

    public void setOperationsMapRepository(Map<String, String> operations) {
        this.operations = operations;
    }

    public String getOperation(String key) {
        return operations.get(key);
    }

    public void addOperation(String key, String value) {
        operations.put(key, value);
    }

    public void removeOperation(String key) {
        operations.remove(key);
    }

    public int size() {
        return operations.size();
    }

    public boolean isEmpty() {
        return operations.isEmpty();
    }

    public void clear() {
        operations.clear();
    }

    public boolean containsKey(String key) {
        return operations.containsKey(key);
    }

    public boolean containsValue(String value) {
        return operations.containsValue(value);
    }

    public boolean containsOperation(String key, String value) {
        return operations.containsKey(key) && operations.containsValue(value);
    }

    public void addOperations(String operationsPath) {
        Map<String, String> operations = InputFileReader.readOperations(operationsPath);

        try {
            if (operations.isEmpty()) {
                throw new Exception("Operations not found");
            }
            this.operations = operations;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
