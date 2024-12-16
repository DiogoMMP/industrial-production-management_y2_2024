package UI.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class provides methods to read a glossary from a file, search for terms in the glossary, and print the results in a table format.
 *
 * @author Diogo Pereira
 */
public class GlossaryService {

    private static final String GLOSSARY_FILE = "documentation/Glossary.md";
    private static int maxTermSize = 0; // Width of the "Term" column
    private static int maxDefSize = 0; // Width of the "Definition" column

    /**
     * Reads the glossary from a file and returns a list of arrays containing the data.
     * @return a list of arrays containing the glossary data
     */
    public static List<String[]> readGlossary() {
        List<String[]> data = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(GLOSSARY_FILE));
            for (int i = 6; i < lines.size(); i++) {
                String[] values = lines.get(i).split("\\|");
                data.add(values);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the CSV file: " + e.getMessage());
        }
        return data;
    }

    /**
     * Searches for a term in the glossary data and returns a set of arrays containing the results.
     * @param data the glossary data
     * @param term the term to search for
     * @return a set of arrays containing the search results
     */
    public static Set<String[]> searchGlossary(List<String[]> data, String term) {
        Set<String[]> results = new LinkedHashSet<>();

        // Search for the term in the file
        for (String[] values : data) {
            String termInFile = values[1].trim().replace("**", "");
            String definition = values[2].trim();

            if (termInFile.equalsIgnoreCase(term.toLowerCase())) {
                results.add(new String[]{termInFile, definition});

                if (termInFile.length() > maxTermSize) {
                    maxTermSize = termInFile.length();
                }

                if (definition.length() > maxDefSize) {
                    maxDefSize = definition.length();
                }

                // If the definition contains "Acronym", search for the term inside quotes
                if (definition.toLowerCase().contains("acronym for")) {
                    int startIndex = definition.indexOf('"');
                    int endIndex = definition.indexOf('"', startIndex + 1);

                    if (startIndex != -1 && endIndex != -1) {
                        String acronymTerm = definition.substring(startIndex + 1, endIndex).trim();

                        // Now search for the definition of this acronym in "data"
                        for (String[] entry : data) {
                            termInFile = entry[1].trim().replace("**", "");
                            definition = entry[2].trim();

                            if (termInFile.equalsIgnoreCase(acronymTerm)) {
                                results.add(new String[]{termInFile, definition});

                                if (termInFile.length() > maxTermSize) {
                                    maxTermSize = termInFile.length();
                                }

                                if (definition.length() > maxDefSize) {
                                    maxDefSize = definition.length();
                                }
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    /**
     * Prints the search results in a table format.
     * @param definitions the search results
     */
    public static void printDefinitionsInTable(Set<String[]> definitions) {
        maxTermSize += 10; // Add 10 spaces for better readability
        maxDefSize += 10; // Add 10 spaces for better readability

        System.out.printf("%n%-" + maxTermSize + "s %-" + maxDefSize + "s%n", "Term", "Definition");
        System.out.println("-".repeat(50 + maxDefSize));

        for (String[] result : definitions) {
            String term = result[0];
            String definition = result[1];

            System.out.printf("%-" + maxTermSize + "s %-" + maxDefSize + "s%n", term, definition);
        }
    }
}

