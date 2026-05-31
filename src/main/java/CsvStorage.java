import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class that helps use CSV files as a persistent storage format for tables
 *
 * Use the static methods in this class to save and load tables to and from CSV files.
 *
 * Example usage:
 * {@snippet :
 * // Assume `table` is an instance of Table, and is already created and populated.
 * try {
 *     CsvStorage.saveTable(table, "products.csv");
 *
 *     // loadTable(tableName, filePath)
 *     Table loaded = CsvStorage.loadTable("products_loaded", "products.csv");
 *
 *     // optional: verify contents
 *     loaded.printTable();
 * } catch (IOException e) {
 *     e.printStackTrace();
 * }
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.06
 */
public class CsvStorage {

    /**
     * Create a CsvStorage object.
     * This constructor is intentionally empty because all methods in this class are static.
     */
    public CsvStorage() {
        // intentionally empty
    }

    /**
     * Save a table from memory into a CSV file.
     *
     * The first line of the CSV file should contain the column headers, in the format "name:type" (e.g. "id:INTEGER,name:STRING,price:DOUBLE").
     * The subsequent lines should contain the row data, with values separated by commas (e.g. "1,Widget,9.99").
     *
     * @param table The table object to be saved
     * @param filePath The path to the CSV file where the table should be saved
     * @throws IOException if there is an error writing to the file
     */
    public static void saveTable(Table table, String filePath)
        throws IOException {
        try (
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))
        ) {
            List<Column> columns = table.getColumns();
            // loop through columns, .toString() for each one
            for (int i = 0; i < columns.size(); i++) {
                writer.write(columns.get(i).toString());
                if (i < columns.size() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

            List<Row> rows = table.getRows();
            for (int i = 0; i < rows.size(); i++) {
                Row currentRow = rows.get(i);
                for (int j = 0; j < currentRow.size(); j++) {
                    Object value = currentRow.getValue(j);

                    writer.write(value.toString());
                    if (j < currentRow.size() - 1) {
                        writer.write(",");
                    }
                }

                writer.newLine();
            }
        }
    }

    /**
     * Load a table from a CSV file into memory as a Table object.
     *
     * @param tableName The name to give the loaded table (this is not stored in the CSV file)
     * @param filePath The path to the CSV file that the table will be loaded from
     * @return the Table object created from the CSV file
     * @throws IOException if there is an error reading from the file
     */
    public static Table loadTable(String tableName, String filePath)
        throws IOException {
        Table table;
        List<Column> columns = new ArrayList<>();

        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath))
        ) {
            // read in only column headers,
            // then parse and initialize table object
            String line = reader.readLine();

            String[] columnStrings = line.split(",");
            for (String col : columnStrings) {
                String[] sep = col.split(":");

                String name = sep[0];
                DataType type = DataType.valueOf(sep[1]);

                columns.add(new Column(name, type));
            }

            // initialize table with new column data
            table = new Table(tableName, columns);

            // loop through remaining rows
            while ((line = reader.readLine()) != null) {
                // construct next row object, append to table
                String[] sep = line.split(",");

                List<Object> values = new ArrayList<>();
                // go through cols and convert to data type before appending to current row object
                for (int i = 0; i < sep.length; i++) {
                    DataType type = columns.get(i).getType();

                    String value = sep[i];

                    // convert value to be type
                    Object convertedValue = switch (type) {
                        case STRING -> value;
                        case INTEGER -> Integer.parseInt(value);
                        case DOUBLE -> Double.parseDouble(value);
                        case BOOLEAN -> Boolean.parseBoolean(value);
                    };

                    values.add(convertedValue);
                }

                // make a Row object out of values, then add to table
                table.addRow(new Row(values));
            }

            return table;
        } catch (TypeMismatchException e) {
            throw new IOException();
        }
    }
}
