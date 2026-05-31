import java.util.ArrayList;
import java.util.List;

/**
 * Represents a transaction that can execute queries on a table and either commit or rollback the changes.
 * The Transaction class maintains an undo log to allow rolling back changes if the transaction is not committed before the commit() method is called.
 * The executeQuery method checks if the query is a SELECT query and only adds to the undo log for non-SELECT queries.
 *
 * Use this class to manage transactions in the mini-database system.
 * This makes it possible to execute queries and either commit or rollback changes as needed.
 *
 * Example usage:
 * {@snippet :
 *      // Assume we have a Table object and a QueryParser object
 *      Table table = ...; // obtain a Table object
 *      QueryParser parser = ...; // obtain a QueryParser object
 *
 *      // Create a new transaction for the table
 *      Transaction transaction = new Transaction(table);
 *
 *      // Execute some queries within the transaction
 *      transaction.executeQuery("INSERT INTO ...", parser);
 *      transaction.executeQuery("UPDATE ...", parser);
 *
 *      // Commit the transaction to save the changes
 *      transaction.commit();
 *
 *      // If we wanted to rollback instead of committing, we would call:
 *      // Reminder: Do not call commit() if you want to rollback, as commit() will clear the undo log and prevent rollback from working.
 *      // transaction.rollback();
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.14
 */
public class Transaction {

    private Table table;
    private boolean isCommitted;
    private List<Runnable> undoLog;

    /**
     * Create a new Transaction for the given table.
     * The transaction starts in an uncommitted state and has an empty undo log.
     *
     * @param table The table that the Transaction will operate on
     */
    public Transaction(Table table) {
        this.table = table;
        this.isCommitted = false;
        this.undoLog = new ArrayList<>();
    }

    /**
     * Execute a query using the provided QueryParser.
     * If the query is not a SELECT query, a snapshot of the current state of the table's rows is taken and added to the undo log before executing the query.
     * SELECT is not included because it doesn't modify the Table
     *
     * @param query The SQL query to execute
     * @param parser The QueryParser to use for executing the query
     */
    public void executeQuery(String query, QueryParser parser) {
        String queryUpper = query.trim().toUpperCase();

        if (!queryUpper.startsWith("SELECT")) {
            // make copy of table's rows
            List<Row> snapshot = new ArrayList<>(this.table.getRows());

            // add to undoLog: query that sets the rows of table to the snapshot that was taken
            this.undoLog.add(() -> this.table.setRows(snapshot));
        }

        parser.execute(query, this.table);
    }

    /**
     * Commit the transaction, making all changes permanent.
     * If the transaction is already committed, an IllegalStateException is thrown.
     * The undoLog is cleared as well, because the changes are now permanent and cannot be rolled back.
     */
    public void commit() {
        if (isCommitted()) {
            throw new IllegalStateException();
        }

        this.isCommitted = true;

        // clear undoLog
        this.undoLog.clear();
    }

    /**
     * Rollback the transaction, undoing all changes made since the transaction was started.
     * If the transaction is already committed, an IllegalStateException is thrown.
     * The undoLog is processed in reverse order to undo the changes, and then cleared after all changes have been undone.
     */
    public void rollback() {
        if (isCommitted()) {
            throw new IllegalStateException();
        }

        for (int i = this.undoLog.size() - 1; i >= 0; i--) {
            this.undoLog.get(i).run();
        }

        this.undoLog.clear();
    }

    /**
     * Check if the transaction has been committed.
     *
     * @return true if the transaction is committed, and false otherwise
     */
    public boolean isCommitted() {
        return isCommitted;
    }
}
