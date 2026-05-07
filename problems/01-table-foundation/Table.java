import java.util.List;
import java.lang.IllegalArgumentException;

public class Table {
	private String name;
	private List<Column> columns;
	private List<Row> rows;

	public Table(String name, List<Column> columns) {
		this.name = name;
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void addRow(Row row) {
		if (row.size() != columns.size()) {
			throw new IllegalArgumentException();
		}

		rows.add(row);
	}

	public List<Row> getRows() {
		return rows;
	}

	public void printTable() {
		System.out.printf("Table: %s\n", name);

		String columnHeader = "";
		for (Column col : columns) {
			columnHeader += (col.toString() + " | "); 
		}
		// remove extra " | "
		columnHeader.substring(0, columnHeader.length() - 3);
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
				int headerLength = columns.get(j).getName().length();
				String spaces = "";
				for (int k = 0; k < headerLength - currentValue.toString().length(); k++) {
					spaces += " ";
				}

				rowString += (currentValue.toString() + spaces + " | ");
			}

			System.out.println(rowString.substring(0, rowString.length() - 3));
		}
	}
}
