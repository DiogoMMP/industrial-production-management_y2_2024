package repository;

import importer_and_exporter.InputFileReader;

import java.util.ArrayList;
import java.util.List;

public class BOORepository {
    private List<String[]> BOO;

    public BOORepository() {
        this.BOO = new ArrayList<>();
    }

    public BOORepository(List<String[]> BOO) {
        this.BOO = BOO;
    }

    public List<String[]> getBOORepository() {
        return BOO;
    }

    public void setBOORepository(List<String[]> BOO) {
        this.BOO = BOO;
    }

    public void addBOO(String[] BOO) {
        this.BOO.add(BOO);
    }

    public void removeBOO(String[] BOO) {
        this.BOO.remove(BOO);
    }

    public int size() {
        return BOO.size();
    }

    public boolean isEmpty() {
        return BOO.isEmpty();
    }

    public void clear() {
        BOO.clear();
    }

    public boolean containsBOO(String[] BOO) {
        return this.BOO.contains(BOO);
    }

    public void addBOOList(String booPath) {
        BOO.clear();
        List<String[]> BOO = InputFileReader.readCsvFile(booPath);
        try {
            if (BOO.isEmpty()) {
                throw new Exception("Bill Of Operations not found");
            }
            this.BOO = BOO;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
