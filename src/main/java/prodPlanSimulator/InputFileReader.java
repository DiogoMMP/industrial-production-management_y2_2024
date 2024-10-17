package prodPlanSimulator;

import prodPlanSimulator.domain.Item;
import prodPlanSimulator.domain.Machine;
import prodPlanSimulator.enums.Priority;

import java.io.*;
import java.util.*;

import static jdk.internal.net.http.common.Log.logError;

public class InputFileReader {

    public static final String FILE_PATH_ITEMS = "src/main/java/prodPlanSimulator/resources/";
    public static final String FILE_PATH_MACHINES = "src/main/java/prodPlanSimulator/resources/";

    public static Map<Integer, Item> readItems(String fileName) {
        Map<Integer, Item> items = new HashMap<>();
        try {
            int increment = 1;
            Scanner scanner = new Scanner(new File(FILE_PATH_ITEMS + fileName));
            scanner.nextLine(); // Skip header line
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                int id = Integer.parseInt(data[0]);
                String priorityStr = data[1].trim();
                Priority priority = Priority.fromString(priorityStr);
                List<String> operations = new ArrayList<>(Arrays.asList(data).subList(2, data.length));
                Item item = new Item(id, priority, new ArrayList<>());
                item.getOperations().addAll(operations);
                items.put(increment, item);
                increment++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found. Please check the file path.");
            logError(e);
        }
        return items;
    }

    public static Map<Integer, Machine> readMachines(String fileName) {
        Map<Integer, Machine> machines = new HashMap<>();
        try {
            int increment = 1;
            Scanner scanner = new Scanner(new File(FILE_PATH_MACHINES + fileName));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                String id = data[0];
                String operation = data[1];
                int time = Integer.parseInt(data[2]);
                Machine machine = new Machine();
                machine.setId(id);
                machine.setOperation(operation);
                machine.setTime(time);
                machines.put(increment, machine);
                increment++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found. Please check the file path.");
            logError(e);
        }
        return machines;
    }
}