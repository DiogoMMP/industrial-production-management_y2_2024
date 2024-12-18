package UI.Domain.USBD.US01;

import UI.Utils.GlossaryService;
import UI.Utils.Utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class US01UI implements Runnable {

    /**
     * Runs the user interface for the US01.
     */
    @Override
    public void run() {

        System.out.println("\n" + Utils.BOLD + Utils.CYAN +
                "--- Search Glossary Terms ------------" + Utils.RESET);

        // Ask the user for the search term
        String searchTerm = Utils.readLineFromConsole(Utils.BOLD + "Enter a term or letter to search: " + Utils.RESET);

        // Get glossary data and search for the term
        List<String[]> data = GlossaryService.readGlossary();
        Set<String[]> results;

        assert searchTerm != null;
        if (searchTerm.length() == 1){
            results = GlossaryService.searchGlossaryFirstLetter(data, searchTerm);
        } else {
            results = GlossaryService.searchGlossary(data, searchTerm);
        }

        // Display the results
        if (!results.isEmpty()) {
            GlossaryService.printDefinitionsInTable(results);
        } else {
            System.out.println("\nNo terms found matching your search.");
        }

        if (Utils.confirm("Do you want to search for another term? (Y/N)")) {
            new US01UI().run();
        }
    }
}
