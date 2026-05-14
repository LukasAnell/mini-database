import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private Table table;
    private boolean isCommitted;
    private List<Runnable> undoLog = new ArrayList<>();

    public Transaction(Table table) {
        this.table = table;
    }

    public void executeQuery(String query, QueryParser parser) {
        String queryUpper = query.trim().toUpperCase();

        // if it startsWith INSERT INTO, make a new query that deletes the INSERTed row with a DELETE query
        if (queryUpper.startsWith("INSERT")) {
            // INSERT INTO tableName VALUES v1, v2, v3, etc.
            // DELETE FROM tableName WHERE v1 op v2

            // get inserted values as comma separated list
            int insertedValuesIndex = queryUpper.indexOf("VALUES") + 7;
            String valuesStr = query.substring(insertedValuesIndex);
            String[] valuesSplit = valuesStr.strip().split(", ");

            // get column name of the value we're going to look for in table
            String colName = this.table.getColumns().get(0).getName();

            // create DELETE query
            String undoQuery = String.format(
                "DELETE FROM %s WHERE %s = %s",
                this.table.getName(),
                colName,
                valuesSplit[0]
            );

            // create Runnable and add to undoLog
            Runnable deleteQuery = () -> parser.execute(undoQuery, this.table);
            this.undoLog.add(deleteQuery);
        } else if (queryUpper.startsWith("DELETE")) {
            // DELETE FROM tableName WHERE v1 op v2
            // SELECT * FROM tableName WHERE v1 op v2

            // get WHERE condition from original DELETE query
            int whereIndex = queryUpper.indexOf("WHERE") + 6;
            String condition = query.substring(whereIndex);

            // create SELECT query
            String selectQuery = String.format(
                "SELECT * FROM %s WHERE %s",
                this.table.getName(),
                condition
            );

            QueryResult preview = parser.execute(selectQuery, this.table);

            // get all rows that would be deleted
            List<Row> deletedRows = new ArrayList<>(preview.getRows());

            // add an operation to undoLog that re-adds all those rows to table
            Runnable addOperation = () -> {
                for (Row row : deletedRows) {
                    table.addRow(row);
                }
            };

            this.undoLog.add(addOperation);
        }
        // Do nothing on SELECT

        parser.execute(query, this.table);
    }

    public void commit() {
        if (isCommitted()) {
            throw new IllegalStateException();
        }

        this.isCommitted = true;

        // clear undoLog
        this.undoLog = new ArrayList<>();
    }

    public void rollback() {
        if (isCommitted()) {
            throw new IllegalStateException();
        }

        for (int i = this.undoLog.size() - 1; i >= 0; i--) {
            this.undoLog.get(i).run();
        }

        this.undoLog = new ArrayList<>();
    }

    public boolean isCommitted() {
        return isCommitted;
    }
}
