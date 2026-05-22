/**
 * Represents a column in a table: the column's name and its data type
 *
 * Use this class to hold information about a single column.
 *
 * Example usage:
 * {@snippet :
 *      Column id = new Column("id", DataType.INTEGER);
 *      String name = id.getName();
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2025.05.06
 */
public class Column {

    private String name;
    private DataType type;

    /**
     * Create a Column with the given name and data type
     *
     * @param name The category of data stored in the column
     * @param type The type of the data that is stored in the column
     */
    public Column(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Get the Column's name
     *
     * @return the Column name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the Column's data type
     *
     * @return the data type of the Column
     */
    public DataType getType() {
        return type;
    }

    /**
     * String representation of the Column in the form {@code name:TYPE}
     *
     * @return a text representation of the Column
     */
    @Override
    public String toString() {
        return name + ":" + type;
    }
}
