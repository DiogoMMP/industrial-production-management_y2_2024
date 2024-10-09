package main;

import main.domain.Item;
import main.domain.Machine;
import main.enums.Priority;

import java.io.*;
import java.util.*;


import static jdk.internal.net.http.common.Log.logError;

public class InputFileReader {

    public static final String FILE_PATH_ITEMS = "src/main/resources/artigos.csv";
    public static final String FILE_PATH_MACHINES = "src/main/resources/maquinas.csv";

    public static Map<Integer, Item> readItems() {
        Map<Integer, Item> items = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File(FILE_PATH_ITEMS));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                Item item = new Item();
                item.setId(Integer.parseInt(data[0]));
                item.setPriority(Priority.fromString(data[1]));

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

    public static Map<String, Machine> readMachines() {
        Map<String, Machine> machines = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File(FILE_PATH_MACHINES));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                Machine machine = new Machine();
                machine.setId(data[0]);
                machine.setOperation(data[1]);
                machine.setTime(Integer.parseInt(data[2]));
                machines.put(machine.getId(), machine);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found. Please check the file path.");
            logError(e);
        }
        return machines;
    }

}
