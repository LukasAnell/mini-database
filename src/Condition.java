/**
 * The Condition class represents a condition used in SQL queries, such as in WHERE clauses.
 * The object represents the column name, operator, and value for a specific condition.
 *
 * Use this class to store information about a condition in a query.
 *
 * Example usage:
 * {@snippet :
 *      // Example: WHERE price > 10
 *      Condition condition = new Condition("price", ">", "10");
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.07
 */
public class Condition {

    private String columnName;
    private String operator;
    private String value;

    /**
     * Create a Condition with the given column name, operator, and value
     *
     * @param columnName The name of the column to which the condition applies (e.g. "price")
     * @param operator The operator for the condition (e.g. ">", "<", "=", "!=")
     * @param value The value to compare against (e.g. "10")
     */
    public Condition(String columnName, String operator, String value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Get the column name for this condition
     *
     * @return the column name for this condition
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Get the operator for this condition
     *
     * @return the operator for this condition
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Get the value for this condition
     *
     * @return the value for this condition
     */
    public String getValue() {
        return value;
    }
}
