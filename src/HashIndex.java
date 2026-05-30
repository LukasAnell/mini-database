/**
 * HashIndex is a simple implementation of a hash-based index for a database table.
 * It allows for efficient lookups, insertions, and deletions based on the values of a specific column.
 * The index is built using a HashMap where the keys are the values from the indexed column, and the values are lists of rows that contain those keys.
 *
 * Example usage:
 * {@snippet :
 *      // Assume we have a Table object with columns and rows
 *      Table table = ...; // obtain a Table object
 *
 *      // Create a HashIndex on the "name" column of the table
 *      HashIndex index = new HashIndex("name", table);
 *
 *      // Lookup rows where the "name" column has the value "Alice"
 *      List<Row> rows = index.lookup("Alice");
 *
 *      // Insert a new row into the index
 *      Row newRow = ...; // create a new Row object
 *      index.insert("Bob", newRow);
 *
 *      // Remove a row from the index
 *      index.remove("Alice", existingRow);
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.08
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashIndex {

    private String columnName;
    private HashMap<Object, List<Row>> index;

    /**
     *
     * @param columnName
     * @param table
     */
    public HashIndex(String columnName, Table table) {
        this.columnName = columnName;
        this.index = new HashMap<>();

        // find columnIndex
        int columnIndex = -1;
        for (int i = 0; i < table.getColumns().size(); i++) {
            if (table.getColumns().get(i).getName().equals(columnName)) {
                columnIndex = i;
                break;
            }
        }

        // build index from table's rows
        for (Row row : table.getRows()) {
            Object key = row.getValue(columnIndex);
            index.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public List<Row> lookup(Object key) {
        return index.getOrDefault(key, new ArrayList<>());
    }

    /**
     *
     * @param key
     * @param row
     */
    public void insert(Object key, Row row) {
        index.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
    }

    /**
     *
     * @param key
     * @param row
     */
    public void remove(Object key, Row row) {
        List<Row> value = index.get(key);

        if (value != null) {
            value.remove(row);
        }
    }

    /**
     *
     * @return
     */
    public String getColumnName() {
        return columnName;
    }
}
