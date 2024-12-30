package UI.Domain;

import UI.Utils.Utils;

import java.io.File;

public class HelpUI implements Runnable {

    private static final String HELP_FILE_PATH = "documentation/CraftFlow_User_Manual.pdf";

    @Override
    public void run() {
        Utils.openInBrowser(new File(HELP_FILE_PATH));
    }
}
