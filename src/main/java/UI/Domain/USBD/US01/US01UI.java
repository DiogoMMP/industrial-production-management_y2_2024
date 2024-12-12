package UI.Domain.USBD.US01;

import UI.Utils.Utils;

import java.io.File;

public class US01UI implements Runnable {

    private static final String GLOSSARY_FILE = "documentation/Glossary.md";

    @Override
    public void run() {
        Utils.openInVSCode(new File(GLOSSARY_FILE));
    }
}
