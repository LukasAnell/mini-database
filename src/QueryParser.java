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
			String[] whereCondition = new String[3];
			System.arraycopy(splitQuery, whereIndex, whereCondition, 0, 3);
			Condition condition = new Condition(whereCondition[0], whereCondition[1], whereCondition[2]);


			List<Row> rows = table.getRows();

			
		} else {
			// contains WHERE, create Condition object and find rows that adhere to condition

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
}
