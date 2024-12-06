package UI.Menu;

import UI.Utils.DataInitializer;

public class StartMenuUI implements Runnable {
    @Override
    public void run() {
        // Adiciona cores usando códigos ANSI
        String reset = "\u001B[0m";        // Reset de cor
        String cyan = "\u001B[36m";       // Texto em ciano
        String bold = "\u001B[1m";        // Texto em negrito
        String red = "\u001B[31m";        // Texto em vermelho

        // ASCII art para o título
        String titleArt = """
                            ╭─────────────────────────────╮
                            │     C R A F T F L O W       │
                            ╰─────────────────────────────╯
                            """;

        // Exibe o menu inicial com formatação
        System.out.printf("%n");
        System.out.println(cyan + bold + titleArt + reset);
        System.out.printf("%n");

        boolean success = false;
        while (!success) {
            try {
                DataInitializer dataInitializer = new DataInitializer();
                dataInitializer.run();
                success = true; // Adicionado para encerrar o loop quando bem-sucedido
            } catch (Exception e) {
                System.err.println(red + "Error: " + e.getMessage() + reset);
            }
        }
    }
}