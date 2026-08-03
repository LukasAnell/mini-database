package org.lukasanell.minidatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

                // Print the result
                System.out.println(result.getMessage());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
