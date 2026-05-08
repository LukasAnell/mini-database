import java.util.List;
import java.util.TreeMap;

public class TreeIndex {
	private String columnName;
	private TreeMap<Object, List<Row>> index;

	public TreeIndex(String columnName, Table table) {
		this.columnName = columnName;

		// build index from table's rows
	}

	public List<Row> lookup(Object key) {
		//

		return null;
	}

	public List<Row> lookupRange(Object low, Object high) {
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
