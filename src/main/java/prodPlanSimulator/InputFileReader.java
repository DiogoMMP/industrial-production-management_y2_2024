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
            Scanner scanner = new Scanner(new File(FILE_PATH_ITEMS + fileName));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                Item item = new Item();
                item.setId(Integer.parseInt(data[0]));
                String priorityStr = data[1].trim();
                item.setPriority(Priority.fromString(priorityStr));

                List<String> operations = new ArrayList<>(Arrays.asList(data).subList(2, data.length));
                item.setOperations(operations);
                items.put(item.getId(), item);
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found. Please check the file path.");
            logError(e);
        }
        return items;
    }

    public static Map<String, Machine> readMachines(String fileName) {
        Map<String, Machine> machines = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File(FILE_PATH_MACHINES + fileName));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                String id = data[0];
                String operation = data[1];
                int time = Integer.parseInt(data[2]);

                Machine machine = machines.getOrDefault(id, new Machine(id, new ArrayList<>(), 0));
                machine.getOperations().add(operation);
                machine.setTime(machine.getTime() + time);
                machines.put(id, machine);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found. Please check the file path.");
            logError(e);
        }
        return machines;
    }
}