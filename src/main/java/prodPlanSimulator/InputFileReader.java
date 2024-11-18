package prodPlanSimulator;

import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Operation;
import prodPlanSimulator.domain.Workstation;
import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.OperationsRepository;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class InputFileReader {

    /**
     * Reads the items from the file and returns a map with the items
     * @param fileName the name of the file to read
     * @return a map with the items
     * @throws FileNotFoundException if the file is not found
     */
    public static Map<Integer, Item> readArticles(String fileName, List<Operation> operationsList, Map<String,String> itemsList) throws FileNotFoundException {
        Map<Integer, Item> items = new HashMap<>();
        int increment = 1;


        InputStream inputStream = InputFileReader.class.getResourceAsStream("/" + fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                String id = data[0].trim();
                String priorityStr = data[1].trim();
                Priority priority = Priority.fromString(priorityStr);
                List<String> operations = new ArrayList<>(Arrays.asList(data).subList(2, data.length));
                Item item = new Item(id, priority, new ArrayList<>());
                for (String operationName : operations) {
                    if (operationsList.stream().anyMatch(o -> o.getId().equals(operationName))) {
                        Operation operation = operationsList.stream().filter(o -> o.getId().equals(operationName)).findFirst().get();
                        item.addOperations(operation.getDescription());
                    } else {
                        throw new IllegalArgumentException("Operation not found: " + operationName);
                    }
                }
                for (String itemKey : itemsList.keySet()) {
                    if (itemKey.equalsIgnoreCase(item.getId())) {
                        item.setDescription(itemsList.get(itemKey));
                        break;
                    }
                }
                items.put(increment, item);
                increment++;
            }
        }

        return items;
    }

    /**
     * Reads the machines from the file and returns a map with the machines
     * @param fileName the name of the file to read
     * @return a map with the machines
     * @throws FileNotFoundException if the file is not found
     */
    public static Map<Integer, Workstation> readMachines(String fileName, List<Operation> operationList) throws FileNotFoundException {
        Map<Integer, Workstation> machines = new HashMap<>();
        int increment = 1;


        InputStream inputStream = InputFileReader.class.getResourceAsStream("/" + fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                String id = data[0];
                String operation = data[1];
                int time = Integer.parseInt(data[2]);
                Workstation workstation = new Workstation();
                workstation.setId(id);
                for (Operation op : operationList) {
                    if (op.getId().equals(operation)) {
                        workstation.setOperation(op);
                        break;
                    }
                }
                workstation.setTime(time);
                machines.put(increment, workstation);
                increment++;
            }
        }

        return machines;
    }

    public static List<Operation> readOperations(String operationsPath) {
        List<Operation> operations = new ArrayList<>();
        InputStream inputStream = InputFileReader.class.getResourceAsStream("/" + operationsPath);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + operationsPath);
        }

        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                String id = data[0];
                String description = data[1];
                Operation operation = new Operation(id, description, 0.0);
                operations.add(operation);
            }
        }
        OperationsRepository operationsRepository = Instances.getInstance().getOperationsRepository();
        for (Operation operation : operations) {
            operationsRepository.addOperation(operation);
        }
        return operations;
    }

    public static Map<String, String> readItems(String itemsPath) {
        Map<String, String> items = new HashMap<>();
        InputStream inputStream = InputFileReader.class.getResourceAsStream("/" + itemsPath);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + itemsPath);
        }

        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                String id = data[0];
                String description = data[1];
                items.put(id, description);
            }
        }

        return items;
    }
}