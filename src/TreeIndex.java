/**
 *
 * @author
 * @version
 * @since
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
