/**
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
     *
     * @param name
     * @param columns
     */
    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     *
     * @param row
     */
    public void addRow(Row row) {
        if (row.size() != columns.size()) {
            throw new IllegalArgumentException();
        }

        rows.add(row);
    }

    /**
     *
     * @return
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     *
     * @param rows
     */
    public void setRows(List<Row> rows) {
        this.rows = new ArrayList<>(rows);
    }

    /**
     *
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
