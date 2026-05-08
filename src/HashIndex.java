import java.util.HashMap;
import java.util.List;

public class HashIndex {
	private String columnName;
	private HashMap<Object, List<Row>> index;

	public HashIndex(String columnName, Table table) {
		this.columnName = columnName;
		// build index from table's rows
	}

	public List<Row> lookup(Object key) {
		//

		return null;
	}

	public void insert(Object key, Row row) {

	}

	public void remove(Object key, Row row) {

	}

	public String getColumnName() {
		return columnName;
	}
}
