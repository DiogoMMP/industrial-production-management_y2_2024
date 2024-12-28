package repository;

import domain.Workstation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class WorkstationRepository {
    private Map<Integer, Workstation> workstations;

    public WorkstationRepository() {
        workstations = new HashMap<>();
    }

    public WorkstationRepository(HashMap<Integer, Workstation> workstations) {
        this.workstations = workstations;
    }

    public void setWorkstations(Map<Integer, Workstation> workstations) {
        this.workstations = workstations;
    }

    public void addWorkstations(HashMap<Integer, Workstation> workstations) {
        this.workstations = workstations;
    }

    public Map<Integer, Workstation> getWorkstations() {
        return workstations;
    }

    public Workstation getWorkstation(int id) {
        return workstations.get(id);
    }

    public void addWorkstation(Workstation workstation) {
        workstations.put(workstations.size() + 1, workstation);
    }

    public void removeWorkstation(int id) {
        workstations.remove(id);
    }

    public void exportMachineToCSV() {
        String fileName = "machineSupervisor/ARQCP/SPRINT3/src/data/machines.csv";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("id,name,min_temp,max_temp,min_humidity,max_humidity\n");
            Random random = new Random();
            Map<Integer, Workstation> workstationsFiltered = getFilteredWorkstations();

            for (Map.Entry<Integer, Workstation> entry : workstationsFiltered.entrySet()) {
                Workstation workstation = entry.getValue();
                double minTemp = 15 + (30 - 15) * random.nextDouble();
                double maxTemp = minTemp + (30 - minTemp) * random.nextDouble();
                double minHumidity = 0 + (100 - 0) * random.nextDouble();
                double maxHumidity = minHumidity + (100 - minHumidity) * random.nextDouble();
                writer.append(String.format(Locale.US, "%s,%s,%.1f,%.1f,%.1f,%.1f\n",
                        workstation.getId(),
                        "M" + workstation.getId(), // Workstation name is not available
                        minTemp,
                        maxTemp,
                        minHumidity,
                        maxHumidity));
            }
        } catch (IOException e) {
            System.out.println("Error exporting workstations to CSV: " + e.getMessage());
        }
    }

    public Map<Integer, Workstation> getFilteredWorkstations() {
        Map<Integer, Workstation> filteredWorkstations = new TreeMap<>();
        for (Map.Entry<Integer, Workstation> entry : workstations.entrySet()) {
            Workstation workstation = entry.getValue();
            int id = Integer.parseInt(workstation.getId());
            filteredWorkstations.put(id, workstation);
        }
        return filteredWorkstations;
    }
}