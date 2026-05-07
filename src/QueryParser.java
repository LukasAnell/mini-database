import java.util.ArrayList;
import java.util.List;

public class QueryParser {
	public static QueryResult execute(String query, Table table) {
		// keywords: SELECT _ FROM, WHERE, INSERT INTO, VALUES, DELETE FROM
		switch (query.split(" ")[0].toUpperCase()) {
			case "SELECT":
				return caseSelect(query, table);
			case "WHERE":
				return caseWhere(query, table);
			case "INSERT":
				return caseInsert(query, table);
			case "DELETE":
				return caseDelete(query, table);
			default:
				// malformed query
				throw new IllegalArgumentException();
		}
	}

	private static QueryResult caseSelect(String query, Table table) {
		String[] splitQuery = query.split(" ");

		// find where FROM is, then grab the term(s) between SELECT and FROM
		int fromIndex = getKeywordIndex(splitQuery, "FROM");

		String[] desiredColumns = new String[fromIndex - 1];
		System.arraycopy(splitQuery, 1, desiredColumns, 0, fromIndex - 1);
		for (int i = 0; i < desiredColumns.length - 1; i++) {
			String currentCol = desiredColumns[i];
			desiredColumns[i] = currentCol.substring(0, currentCol.length() - 1);
		}

		// not sure what this is for yet
		String tableName = splitQuery[fromIndex + 1];

		int whereIndex = getKeywordIndex(splitQuery, "WHERE");
		if (whereIndex == -1) {
			// no WHERE keyword, grab every entry from requested columns
			// 
		} else {
			// contains WHERE, create Condition object and find rows that adhere to condition
			String[] whereCondition = new String[3];
			System.arraycopy(splitQuery, whereIndex, whereCondition, 0, 3);
			Condition condition = new Condition(whereCondition[0], whereCondition[1], whereCondition[2]); // columnName, operator, value

			List<Row> rows = table.getRows();

			int conditionColIndex = getColumnIndex(table.getColumns(), condition.getColumnName());

			List<Row> result = new ArrayList<>();
			for (int i = 0; i < rows.size(); i++) {
				
				Row currentRow = rows.get(i);
				Object rowColValue = currentRow.getValue(conditionColIndex);
				// check if value of row at column name would return true for condition
				switch (condition.getOperator()) {
					case "=":
						if (rowColValue.equals(condition.getValue())) {
							// make new row from earlier desired columns, add to result
							Row selectedValuesRow = getSelectedColumns(table.getColumns(), desiredColumns, currentRow);
							// add row to result
							result.add(selectedValuesRow);
						}
						break;
					case "<":

						break;
					case ">":

						break;
				}
			}
		}

		return null;
	}

	private static QueryResult caseWhere(String query, Table table) {

		return null;
	}

	private static QueryResult caseInsert(String query, Table table) {

		return null;
	}

	private static QueryResult caseDelete(String query, Table table) {

		return null;
	}

	private static int getKeywordIndex(String[] splitQuery, String keyword) {
		for (int i = 0; i < splitQuery.length; i++) {
			if (splitQuery[i].equalsIgnoreCase(keyword)) {
				return i;
			}
		}

		return -1;
	}

	private static int getColumnIndex(List<Column> columns, String columnName) {
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getName().equals(columnName)) {
				return i;
			}
		}

		return -1;
	}

	private static Row getSelectedColumns(List<Column> columns, String[] selectedColumns, Row row) {
		// check if * was selected
		if (selectedColumns[0] == "*") {
			return row;
		}

		int[] columnIndices = new int[selectedColumns.length];
		for (int i = 0; i < selectedColumns.length; i++) {
			columnIndices[i] = getColumnIndex(columns, selectedColumns[i]);
		}

		List<Object> values = new ArrayList<>();
		for (int index : columnIndices) {
			values.add(row.getValue(index));
		}

		return new Row(values);
	}
}
