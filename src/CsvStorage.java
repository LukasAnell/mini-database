/**
 *
 * @author
 * @version
 * @since
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvStorage {

    /**
     *
     * @param table
     * @param filePath
     * @throws IOException
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
     *
     * @param tableName
     * @param filePath
     * @return
     * @throws IOException
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
        }
    }
}
