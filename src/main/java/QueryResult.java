import java.util.List;

/**
 * The QueryResult class represents the result of a query execution in the mini-database system.
 * It contains a list of rows that match the query criteria and a message indicating the status of the query execution.
 *
 * Use this class to store and access the results of a query, including both the matching rows and messages about the execution status.
 *
 * Example usage:
 * {@snippet :
 *      // Assume we have a list of rows and a message from executing a query
 *      List<Row> rows = ...; // obtained from query execution
 *      String message = "Query executed successfully";
 *
 *      // Create a QueryResult object to store the results
 *      QueryResult result = new QueryResult(rows, message);
 *
 *      // Access the rows and message from the QueryResult
 *      List<Row> resultRows = result.getRows();
 *      String resultMessage = result.getMessage();
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.07
 */
public class QueryResult {

    private List<Row> rows;
    private String message;

    /**
     * Create a QueryResult with the given list of rows and message
     *
     * @param rows The list of rows that match the query criteria
     * @param message A message indicating the status of the query execution (e.g. "Query executed successfully", "No rows found", "Error: Invalid query syntax")
     */
    public QueryResult(List<Row> rows, String message) {
        this.rows = rows;
        this.message = message;
    }

    /**
     * Get the list of rows that match the query criteria
     *
     * @return the list of rows that match the query criteria
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Get the message indicating the status of the query execution
     *
     * @return the message indicating the status of the query execution
     */
    public String getMessage() {
        return message;
    }
}
