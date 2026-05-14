import java.util.List;

public class QueryResult {

    private List<Row> rows;
    private String message;

    public QueryResult(List<Row> rows, String message) {
        this.rows = rows;
        this.message = message;
    }

    public List<Row> getRows() {
        return rows;
    }

    public String getMessage() {
        return message;
    }
}
