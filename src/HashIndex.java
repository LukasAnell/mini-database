import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashIndex {
	private String columnName;
	private HashMap<Object, List<Row>> index;

	public HashIndex(String columnName, Table table) {
		this.columnName = columnName;

		// build index from table's rows
		List<Row> rows = table.getRows();
		for (Row row : rows) {
			// for each row, loop through values and add to HashMap
			for (int i = 0; i < table.getColumns().size(); i++) {
				Object key = row.getValue(i);

				// if value doesn't exist yet, create a new List
				// otherwise, append row onto existing List
				index.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
			}
		}
	}

	public List<Row> lookup(Object key) {
		return index.get(key);
	}

	public void insert(Object key, Row row) {
		index.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
	}

	public void remove(Object key, Row row) {
		List<Row> value = index.get(key);

		value.remove(row);

		index.put(key, value);
	}

	public String getColumnName() {
		return columnName;
	}
}
