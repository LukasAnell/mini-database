/**
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
