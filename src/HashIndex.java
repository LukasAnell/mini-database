/**
 *
 * @author
 * @version
 * @since
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
