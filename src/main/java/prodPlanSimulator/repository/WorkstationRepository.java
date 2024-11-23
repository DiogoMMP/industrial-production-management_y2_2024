package prodPlanSimulator.repository;

import prodPlanSimulator.domain.Workstation;

import java.util.HashMap;
import java.util.Map;

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
}
