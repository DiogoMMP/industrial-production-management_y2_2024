package prodPlanSimulator.UI.Utils;

import prodPlanSimulator.repository.HashMap_Items_Machines;
import prodPlanSimulator.repository.Instances;

public class DataInitializer implements Runnable{
    private HashMap_Items_Machines map = Instances.getInstance().getHashMapItemsWorkstations();

    public void init(String pathArt, String pathWor){
        map.addAll(pathArt, pathWor);
    }

    @Override
    public void run() {
        System.out.println("Please insert the paths to the files with the data:");
        String pathArt = Utils.readLineFromConsole("Articles: ");
        String pathWor = Utils.readLineFromConsole("Workstations: ");
        init(pathArt,pathWor);
    }
}
