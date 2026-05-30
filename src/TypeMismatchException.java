public class TypeMismatchException extends RuntimeException {

    private String columnName;
    private DataType expectedType;
    private String actualValue;

    public TypeMismatchException(
        String columnName,
        DataType expectedType,
        String actualValue
    ) {
        this.columnName = columnName;
        this.expectedType = expectedType;
        this.actualValue = actualValue;
    }

    @Override
    public String getMessage() {
        return String.format(
            "Type mismatch for column '%s': expected %s but got '%s'",
            columnName,
            expectedType,
            actualValue
        );
    }
}
