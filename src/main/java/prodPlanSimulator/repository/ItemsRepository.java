package prodPlanSimulator.repository;

import prodPlanSimulator.InputFileReader;

import java.util.HashMap;
import java.util.Map;

public class ItemsRepository {
    Map<String, String> items;

    public ItemsRepository(Map<String, String> items) {
        this.items = items;
    }

    public ItemsRepository() {
        this.items = new HashMap<>();
    }

    public Map<String, String> getItemsRepository() {
        return items;
    }

    public void setItemsRepository(Map<String, String> items) {
        this.items = items;
    }

    public String getItem(String key) {
        return items.get(key);
    }

    public String getItemValue(String value) {
        for (Map.Entry<String, String> entry : items.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void addItem(String key, String value) {
        items.put(key, value);
    }

    public void removeItem(String key) {
        items.remove(key);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    public boolean containsKey(String key) {
        return items.containsKey(key);
    }

    public boolean containsValue(String value) {
        return items.containsValue(value);
    }

    public boolean containsItem(String key, String value) {
        return items.containsKey(key) && items.containsValue(value);
    }

    public void addItems(String itemsPath) {
        Map<String, String> items = InputFileReader.readItems(itemsPath);

        try {
            if (items.isEmpty()) {
                throw new Exception("Items not found");
            }
            this.items = items;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }


}
