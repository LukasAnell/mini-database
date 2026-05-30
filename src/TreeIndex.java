/**
 * TreeIndex is a simple implementation of a tree-based index for a database table.
 * It uses a TreeMap to store the index, allowing for efficient lookups and range queries based on the indexed column's values.
 * The index is built from the existing rows in the table, and it supports lookups for specific values as well as range queries.
 *
 * Use this class to create an index on a specific column of a table.
 * The index can be used to perform lookups for specific values and range queries based on the indexed values.
 *
 * Example usage:
 * {@snippet :
 *      // Assume we have a Table object with columns and rows
 *      Table table = ...; // obtain a Table object
 *
 *      // Create a TreeIndex on the "age" column of the table
 *      TreeIndex index = new TreeIndex("age", table);
 *
 *      // Lookup rows where the "age" column has the value 30
 *      List<Row> rows = index.lookup(30);
 *
 *      // Lookup rows where the "age" column is between 20 and 40
 *      List<Row> rangeRows = index.lookupRange(20, 40);
 *
 *      // Insert a new row into the index
 *      Row newRow = ...; // create a new Row object
 *      index.insert(25, newRow);
 *
 *      // Remove a row from the index
 *      index.remove(30, existingRow);
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.11
 */
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TreeIndex {

    private String columnName;
    private TreeMap<Object, List<Row>> index;

    /**
     *
     * @param columnName
     * @param table
     */
    public TreeIndex(String columnName, Table table) {
        this.columnName = columnName;

        @SuppressWarnings("unchecked")
        TreeMap<Object, List<Row>> temp = new TreeMap<>((a, b) ->
            ((Comparable<Object>) a).compareTo(b)
        );

        this.index = temp;

        // find column index
        List<Column> columns = table.getColumns();

        int columnIndex = -1;
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equals(columnName)) {
                columnIndex = i;
                break;
            }
        }

        // build the index from existing rows
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
     * @param low
     * @param high
     * @return
     */
    public List<Row> lookupRange(Object low, Object high) {
        List<Row> rows = new ArrayList<>();

        for (List<Row> bucket : index.subMap(low, true, high, true).values()) {
            rows.addAll(bucket);
        }

        return rows;
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
        List<Row> rows = index.get(key);
        if (rows != null) {
            rows.remove(row);
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
