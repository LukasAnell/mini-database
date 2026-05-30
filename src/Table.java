/**
 * Represents a table: a title for the table, a list of columns, and a list of rows
 *
 * Use this class to store information about a whole table
 *
 * Example usage:
 * {@snippet :
 *      List<Column> columns = Arrays.asList(
 *          new Column("id", DataType.INTEGER),
 *          new Column("name", DataType.STRING),
 *          new Column("price", DataType.DOUBLE)
 *      );
 *
 *      // Create the table
 *      Table table = new Table("products", columns);
 *
 *      // Add rows (each Row must have the same number of values as columns)
 *      table.addRow(new Row(Arrays.asList(1, "Widget", 9.99)));
 *      table.addRow(new Row(Arrays.asList(2, "Gadget", 14.50)));
 *
 *      // Print the table
 *      table.printTable();
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.06
 */
import java.util.ArrayList;
import java.util.List;

public class Table {

    private String name;
    private List<Column> columns;
    private List<Row> rows = new ArrayList<>();

    /**
     * Create a Table with the given name and columns
     *
     * @param name The title of the table
     * @param columns The list of columns in Table
     */
    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    /**
     * Get the Table's name
     *
     * @return the Table name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the list of Columns in the Table
     *
     * @return the list of Columns in the Table
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Add a new Row of data to the list of Rows in the Table. Each element in the Row corresponds to a Column in the Table
     *
     * @param row The row that will be added to the Table's list of Rows
     */
    public void addRow(Row row) {
        if (row.size() != columns.size()) {
            throw new IllegalArgumentException();
        }

        // validate the DataType of each element in row, to make sure it matches the DataType of the matching column
        for (int i = 0; i < row.size(); i++) {
            Object rowValue = row.getValue(i);
            Column currentColumn = columns.get(i);

            if (!currentColumn.getType().equals(rowValue)) {
                throw new TypeMismatchException(
                    currentColumn.getName(),
                    currentColumn.getType(),
                    rowValue.toString()
                );
            }
        }

        rows.add(row);
    }

    /**
     * Get the list of Rows in the Table
     *
     * @return the list of Rows in the Table
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Set the list of Rows in the Table to a different list of Rows
     *
     * @param rows The new list of Rows that will be stored in Table
     */
    public void setRows(List<Row> rows) {
        this.rows = new ArrayList<>(rows);
    }

    /**
     * Print a string representation of the Table.
     * Formatted to show the title, the name and type of the columns, and the values stored in the list of rows.
     */
    public void printTable() {
        System.out.printf("Table: %s\n", name);

        StringBuilder columnHeader = new StringBuilder();
        for (Column col : columns) {
            columnHeader.append(col.toString()).append(" | ");
        }
        // trim last separator
        columnHeader.setLength(columnHeader.length() - 3);
        System.out.println(columnHeader);

        String spacer = "--";
        for (int i = 0; i < columnHeader.length(); i++) {
            spacer += "-";
        }
        System.out.println(spacer);

        for (int i = 0; i < rows.size(); i++) {
            String rowString = "";

            Row currentRow = rows.get(i);
            for (int j = 0; j < currentRow.size(); j++) {
                Object currentValue = currentRow.getValue(j);

                // length of current column's header
                // so we can add spaces to entry, so it lines up
                int headerLength = columns.get(j).toString().length();
                String spaces = "";
                for (
                    int k = 0;
                    k < headerLength - currentValue.toString().length();
                    k++
                ) {
                    spaces += " ";
                }

                rowString += (currentValue.toString() + spaces + " | ");
            }

            System.out.println(rowString.substring(0, rowString.length() - 3));
        }
    }
}
