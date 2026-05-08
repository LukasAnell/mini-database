import java.util.ArrayList;
import java.util.List;

public class QueryParser {
	public QueryResult execute(String query, Table table) {
		// keywords: SELECT _ FROM, WHERE, INSERT INTO, VALUES, DELETE FROM
		switch (query.split(" ")[0].toUpperCase()) {
			case "SELECT":
				return caseSelect(query, table);
			case "INSERT":
				return caseInsert(query, table);
			case "DELETE":
				return caseDelete(query, table);
			default:
				// malformed query
				throw new IllegalArgumentException();
		}
	}

	private QueryResult caseSelect(String query, Table table) {
		return null;
	}	

	private QueryResult caseInsert(String query, Table table) {

		return null;
	}

	private QueryResult caseDelete(String query, Table table) {

		return null;
	}
}
