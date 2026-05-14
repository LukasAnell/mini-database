import java.util.List;

public class Transaction {
	private Table table;
	private boolean isCommitted;
	private List<Runnable> undoLog;

	public Transaction(Table table) {
		this.table = table;
	}

	public void executeQuery(String query, QueryParser parser) {
		String queryUpper = query.trim().toUpperCase();

		QueryResult result = parser.execute(query, this.table);

		if (queryUpper.startsWith("INSERT")) {
			// figure out what was inserted, then add a runnable to the undoLog that would remove it
		} else if (queryUpper.startsWith("DELETE")) {
			// need to somehow get the row that was deleted before it's deleted
			// I think alter query to be a SELECT query, and then store that in undoLog before the original query is executed
		}
		// Do nothing on SELECT
	}

	public void commit() {
		//
	}

	public void rollback() {
		//
	}

	public boolean isCommitted() {
		return isCommitted;
	}
}
