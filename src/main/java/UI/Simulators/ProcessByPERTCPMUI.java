package UI.Simulators;

import UI.Menu.MainMenuPERTCPM;


public class ProcessByPERTCPMUI implements Runnable {

    @Override
    public void run() {
        MainMenuPERTCPM mainMenuPERTCPM = new MainMenuPERTCPM();
        mainMenuPERTCPM.run();
    }
}
