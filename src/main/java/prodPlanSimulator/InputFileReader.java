package prodPlanSimulator;

import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Workstation;
import prodPlanSimulator.enums.Priority;

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
    public static Map<Integer, Item> readItems(String fileName) throws FileNotFoundException {
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
                int id = Integer.parseInt(data[0]);
                String priorityStr = data[1].trim();
                Priority priority = Priority.fromString(priorityStr);
                List<String> operations = new ArrayList<>(Arrays.asList(data).subList(2, data.length));
                Item item = new Item(id, priority, new ArrayList<>());
                // item.getOperations().addAll(operations);
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
    public static Map<Integer, Workstation> readMachines(String fileName) throws FileNotFoundException {
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
                // workstation.setOperation(operation);
                workstation.setTime(time);
                machines.put(increment, workstation);
                increment++;
            }
        }

        return machines;
    }
}