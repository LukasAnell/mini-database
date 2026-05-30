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
     *
     * @param columnName
     * @param operator
     * @param value
     */
    public Condition(String columnName, String operator, String value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    /**
     *
     * @return
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     *
     * @return
     */
    public String getOperator() {
        return operator;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return value;
    }
}
