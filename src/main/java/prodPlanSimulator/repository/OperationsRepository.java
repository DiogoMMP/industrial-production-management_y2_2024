package prodPlanSimulator.repository;

import domain.Operation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OperationsRepository {
    private List<Operation> operations;

    public OperationsRepository(List<Operation> operations) {
        this.operations = operations;
    }

    public OperationsRepository() {
        this.operations = new ArrayList<>();
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperationsList(List<Operation> operations) {
        this.operations = operations;
    }

    public Operation getOperation(int index) {
        return operations.get(index);
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
    }

    public void removeOperation(Operation operation) {
        operations.remove(operation);
    }

    public void removeOperation(int index) {
        operations.remove(index);
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

    public boolean contains(Operation operation) {
        return operations.contains(operation);
    }

    public boolean containsAll(List<Operation> operations) {
        return new HashSet<>(this.operations).containsAll(operations);
    }

    public Operation getOperationByName(String name) {
        for (Operation operation : operations) {
            if (operation.getDescription().equalsIgnoreCase(name)) {
                return operation;
            }
        }
        return new Operation();
    }
}
