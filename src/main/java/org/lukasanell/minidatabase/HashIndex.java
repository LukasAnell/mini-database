package org.lukasanell.minidatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * HashIndex is a simple implementation of a hash-based index for a database table.
 * It allows for efficient lookups, insertions, and deletions based on the values of a specific column.
 * The index is built using a HashMap where the keys are the values from the indexed column, and the values are lists of rows that contain those keys.
 *
 * Use this class to create an index on a specific column of a table.
 * The index can be used to perform lookups, insertions, and deletions based on the indexed values.
 *
 * Example usage:
 * {@snippet :
 * // Assume we have a Table object with columns and rows
 * Table table = ...; // obtain a Table object
 *
 * // Create a HashIndex on the "name" column of the table
 * HashIndex index = new HashIndex("name", table);
 *
 * // Lookup rows where the "name" column has the value "Alice"
 * List<Row> rows = index.lookup("Alice");
 *
 * // Insert a new row into the index
 * Row newRow = ...; // create a new Row object
 * index.insert("Bob", newRow);
 *
 * // Remove a row from the index
 * index.remove("Alice", existingRow);
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.08
 */
public class HashIndex {

    private String columnName;
    private HashMap<Object, List<Row>> index;

    /**
     * Create a HashIndex for the specified column in the given table.
     * The index is built from the existing rows in the table.
     *
     * @param columnName The name of the column to index
     * @param table The table from which to build the index
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
     * Lookup rows in the index that match the given key.
     *
     * @param key The value to look up in the indexed column
     * @return A list of rows that match the given key, or an empty list if no matches are found
     */
    public List<Row> lookup(Object key) {
        return index.getOrDefault(key, new ArrayList<>());
    }

    /**
     * Insert a new row into the index for the given key.
     * If the key already exists, the row is added to the existing list of rows for that key.
     *
     * @param key The value of the indexed column for the new row
     * @param row The row to insert into the index
     */
    public void insert(Object key, Row row) {
        index.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
    }

    /**
     * Remove a row from the index for the given key.
     *
     * @param key The value of the indexed column for the row to remove
     * @param row The row to remove from the index
     */
    public void remove(Object key, Row row) {
        List<Row> value = index.get(key);

        if (value != null) {
            value.remove(row);
        }
    }

    /**
     * Get the name of the indexed column.
     *
     * @return the name of the indexed column
     */
    public String getColumnName() {
        return columnName;
    }
}
