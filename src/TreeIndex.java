import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TreeIndex {
	private String columnName;
	private TreeMap<Object, List<Row>> index;

	public TreeIndex(String columnName, Table table) {
		this.columnName = columnName;
		this.index = new TreeMap<>((a, b) -> ((Comparable<Object>) a).compareTo(b));

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

	public List<Row> lookup(Object key) {
		return index.getOrDefault(key, new ArrayList<>());
	}

	public List<Row> lookupRange(Object low, Object high) {
		List<Row> rows = new ArrayList<>();

		for (List<Row> bucket : index.subMap(low, true, high, true).values()) {
			rows.addAll(bucket);
		}

		return rows;
	}

	public void insert(Object key, Row row) {
		index.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
	}

	public void remove(Object key, Row row) {
		List<Row> rows = index.get(key);
		if (rows != null) {
			rows.remove(row);
		}
	}

	public String getColumnName() {
		return columnName;
	}
}
