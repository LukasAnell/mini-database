import java.util.List;

/**
 * Represents a row in a table: a list of values that corresponds to the columns of the table
 *
 * Use this class to store information about a row in a table
 *
 * Example usage:
 * {@snippet :
 *      List<Object> values = Arrays.asList("Jeff", 25, "Engineer");
 *      Row row = new Row(values);
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.06
 */
public class Row {

    private List<Object> values;

    /**
     * Create a Row with the given list of values
     *
     * @param values The list of values in the Row, which correspond to the Column of the Table
     */
    public Row(List<Object> values) {
        this.values = values;
    }

    /**
     * Get the value at the specified index in the Row
     *
     * @param index The index of the value to retrieve, which corresponds to the Column index in the Table
     * @return the value at the specified index in the Row
     */
    public Object getValue(int index) {
        return values.get(index);
    }

    /**
     * Get the number of values in the Row
     *
     * @return the number of values in the Row
     */
    public int size() {
        return values.size();
    }

    /**
     * Return a string representation of the Row, which is a comma-separated list of the values in the Row
     *
     * @return a string representation of the Row
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i < values.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
