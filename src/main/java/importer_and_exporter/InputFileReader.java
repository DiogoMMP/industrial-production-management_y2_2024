package importer_and_exporter;

import domain.*;
import enums.Priority;
import repository.Instances;
import repository.OperationsRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Reads the operations from a CSV file and returns a list of Operation objects.
     * @param operationsPath the path to the CSV file with the operations
     * @return a list of Operation objects
     */
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

    /**
     * Reads a CSV file and returns its data as a list of string arrays.
     * @param filePath the path to the CSV file
     * @return a list of string arrays representing the data in the CSV file
     */
    public static List<String[]> readCsvFileCommas(String filePath) {
        List<String[]> data = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILES_PATH + filePath));
            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i).split(",");
                data.add(values);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the CSV file: " + e.getMessage());
        }
        return data;
    }

    /**
     * Reads activities from a CSV file and returns a map of activity IDs to Activity objects.
     * @param activitiesFileName the path to the CSV file with the activities
     * @return a map of activity IDs to Activity objects
     */
    public static Map<String, Activity> readActivities(String activitiesFileName) {
        List<String[]> data = readCsvFileCommas(activitiesFileName);
        Map<String, Activity> activities = new HashMap<>();

        for (String[] activityFile : data) {

            // Get the list of dependencies (remove quotes and divide by commas)
            List<String> prevActIds = new ArrayList<>();
            if (activityFile.length > 5 && !activityFile[5].isEmpty()) {
                prevActIds = Arrays.stream(activityFile[5].replace("\"", "").split(","))
                        .map(String::trim) // Remove blank spaces
                        .filter(s -> !s.isEmpty()) // Avoid empty dependencies
                        .collect(Collectors.toList());
            }

            try {
                // Create the ‘Activity’ object using the correct constructor
                Activity activity = new Activity(
                        activityFile[0],                           // ActivKey (actId)
                        activityFile[1],                           // Description
                        Integer.parseInt(activityFile[2]),         // Duration
                        activityFile[3],                           // Duration Unit
                        Integer.parseInt(activityFile[4]),         // Total Cost
                        prevActIds                                 // List of dependencies
                );

                // Add the activity to the map
                activities.put(activityFile[0], activity);

            } catch (NumberFormatException e) {
                System.err.println("Error converting numeric data in the row: " + Arrays.toString(activityFile));
            } catch (Exception e) {
                System.err.println("Error processing the line: " + Arrays.toString(activityFile));
            }
        }

        return activities;
    }

    /**
     * Reads the orders from a CSV file and returns a list of Order objects.
     * @param ordersFileName the path to the CSV file with the orders
     * @return a list of Order objects
     */
    public static ArrayList<Order> readOrders(String ordersFileName) {
        List<String[]> data = readCsvFileCommas(ordersFileName);
        Map<Integer, Order> ordersMap = new HashMap<>();
        for (String[] orderFile : data) {
            int id = Integer.parseInt(orderFile[0]);
            String itemId = orderFile[1];
            Priority priority = Priority.fromString(orderFile[2]);
            int quantity = Integer.parseInt(orderFile[3]);

            Order order = ordersMap.get(id);
            if (order == null) {
                List<String> itemsIdList = new ArrayList<>();
                List<Integer> quantities = new ArrayList<>();
                order = new Order(itemsIdList, id, priority, quantities);
                ordersMap.put(id, order);
            }
            order.addItemsId(itemId);
            order.getQuantity().add(quantity);
        }

        return new ArrayList<>(ordersMap.values());
    }

}