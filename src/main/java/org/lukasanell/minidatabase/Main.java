package org.lukasanell.minidatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the mini-database interactive REPL.
 * Reads SQL-like queries from stdin, executes them against an in-memory Table via QueryParser, and prints the results.
 *
 * Type "exit" or "quit" to end the session.
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.08.03
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static Table table = new Table(
        "students",
        new ArrayList<>(
            List.of(
                new Column("id", DataType.INTEGER),
                new Column("name", DataType.STRING),
                new Column("gpa", DataType.DOUBLE)
            )
        )
    );
    private static QueryParser parser = new QueryParser();

    /**
     * Main method: starts the REPL loop, reading user input, executing queries, and printing results.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            // Skip empty input
            if (input.trim().isEmpty()) {
                continue;
            }

            // Check for exit command
            if (
                input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")
            ) {
                System.out.println("Exiting...");
                break;
            }

            try {
                // Parse the input and execute the query
                QueryResult result = parser.execute(input, table);

                printResult(result);
            } catch (Exception e) {
                String errorMessage =
                    e.getMessage() != null
                        ? e.getMessage()
                        : e.getClass().getSimpleName();

                System.out.println("Error: " + errorMessage);
            }
        }
    }

    /**
     * Print a QueryResult: a header line, followed by each row, followed by the result message.
     *
     * @param result The QueryResult to print
     */
    private static void printResult(QueryResult result) {
        List<Row> rows = result.getRows();

        if (rows != null && !rows.isEmpty()) {
            if (rows.get(0).size() == table.getColumns().size()) {
                StringBuilder header = new StringBuilder();

                for (int i = 0; i < table.getColumns().size(); i++) {
                    header.append(table.getColumns().get(i).getName());

                    if (i < table.getColumns().size() - 1) {
                        header.append(", ");
                    }
                }

                System.out.println(header);
            }

            for (Row row : rows) {
                System.out.println(row);
            }
        }

        System.out.println(result.getMessage());
    }
}
