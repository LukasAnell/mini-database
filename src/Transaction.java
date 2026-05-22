/**
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.14
 */
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private Table table;
    private boolean isCommitted;
    private List<Runnable> undoLog;

    /**
     *
     * @param table
     */
    public Transaction(Table table) {
        this.table = table;
        this.isCommitted = false;
        this.undoLog = new ArrayList<>();
    }

    /**
     *
     * @param query
     * @param parser
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
     *
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
     *
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
     *
     * @return
     */
    public boolean isCommitted() {
        return isCommitted;
    }
}
