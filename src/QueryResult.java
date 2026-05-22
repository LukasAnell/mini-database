/**
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2025.05.07
 */
import java.util.List;

public class QueryResult {

    private List<Row> rows;
    private String message;

    /**
     *
     * @param rows
     * @param message
     */
    public QueryResult(List<Row> rows, String message) {
        this.rows = rows;
        this.message = message;
    }

    /**
     *
     * @return
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return message;
    }
}
