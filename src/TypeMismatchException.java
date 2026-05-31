/**
 * Exception thrown when a value does not match the expected data type for a column.
 * Includes details about the column name, expected data type, and actual value that caused the mismatch.
 *
 * This exception is used to provide clear error messages when type mismatches occur during data insertion or manipulation in the Table.
 *
 * Example usage:
 * { @snippet :
 *      try {
 *          // Attempt to add a row with a type mismatch (e.g., inserting a string into an integer column)
 *          table.addRow(new Row(List.of("Alice", "not_a_number")));
 *      } catch (TypeMismatchException e) {
 *          System.out.println(e.getMessage());
 *      }
 * }
 */
public class TypeMismatchException extends RuntimeException {

    private String columnName;
    private DataType expectedType;
    private String actualValue;

    /**
     * Constructor for TypeMismatchException that initializes the column name, expected data type, and actual value that caused the mismatch.
     *
     * @param columnName The name of the column where the type mismatch occurred
     * @param expectedType The expected data type for the column
     * @param actualValue The actual value that was provided, which caused the type mismatch
     */
    public TypeMismatchException(
        String columnName,
        DataType expectedType,
        String actualValue
    ) {
        super(
            String.format(
                "Type mismatch for column '%s': expected %s but got '%s'",
                columnName,
                expectedType,
                actualValue
            )
        );
        this.columnName = columnName;
        this.expectedType = expectedType;
        this.actualValue = actualValue;
    }

    /**
     * Get the name of the column where the type mismatch occurred.
     *
     * @return name of the column where the type mismatch occurred
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Get the expected data type for the column where the type mismatch occurred.
     *
     * @return the expected data type for the column where the type mismatch occurred
     */
    public DataType getExpectedType() {
        return expectedType;
    }

    /**
     * Get the actual value that was provided
     *
     * @return the actual value that was provided
     */
    public String getActualValue() {
        return actualValue;
    }
}
