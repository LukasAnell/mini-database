public class Condition {

    private String columnName;
    private String operator;
    private String value;

    public Condition(String columnName, String operator, String value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }
}
