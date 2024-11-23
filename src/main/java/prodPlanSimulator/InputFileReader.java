package prodPlanSimulator;

import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Operation;
import prodPlanSimulator.domain.Workstation;
import prodPlanSimulator.enums.Priority;
import prodPlanSimulator.repository.Instances;
import prodPlanSimulator.repository.OperationsRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class InputFileReader {

    private static final String FILES_PATH = "src/main/resources/";

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
                    if (op.getDescription().equalsIgnoreCase(operation)) {
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

    public static List<Operation> readListOperations(String operationsPath) {
        List<Operation> operations = new ArrayList<>();
        InputStream inputStream = InputFileReader.class.getResourceAsStream("/" + operationsPath);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + operationsPath);
        }

        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                String id = data[0].trim();
                String description = data[1].trim();
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

    /**
     * Reads the materials from a CSV file and returns a map of item IDs to item names.
     * @param itemsFileName the path to the CSV file with the items
     * @return a map of item IDs to item names
     */
    public static Map<String, String> readItems(String itemsFileName) {
        List<String[]> itemsData = readCsvFile(itemsFileName);
        Map<String, String> itemNames = new HashMap<>();
        for (String[] item : itemsData) {
            if (item.length >= 2) {
                itemNames.put(item[0], item[1]);
            }
        }
        return itemNames;
    }

    /**
     * Reads the operations from a CSV file and returns a map of operation IDs to operation descriptions.
     * @param operationFileName the path to the CSV file with the operations
     * @return a map of operation IDs to operation descriptions
     */
    public static Map<String, String> readOperations(String operationFileName) {
        List<String[]> operationsData = readCsvFile(operationFileName);
        Map<String, String> operationDescriptions = new HashMap<>();
        for (String[] operation : operationsData) {
            if (operation.length >= 2) {
                String id = operation[0].trim();
                String description = operation[1].trim();
                operationDescriptions.put(id, description);
            }
        }

        return operationDescriptions;
    }

    /**
     * Reads a CSV file and returns its data as a list of string arrays.
     * @param filePath the path to the CSV file
     * @return a list of string arrays representing the data in the CSV file
     */
    public static List<String[]> readCsvFile(String filePath) {
        List<String[]> data = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILES_PATH + filePath));
            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i).split(";");
                data.add(values);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the CSV file: " + e.getMessage());
        }
        return data;
    }

}