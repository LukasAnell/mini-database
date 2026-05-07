import java.util.List;

public class Main {
	public static void main(String[] args) {
		List<Column> columns = List.of(
				new Column("id", DataType.INTEGER),
				new Column("name", DataType.STRING),
				new Column("gpa", DataType.DOUBLE)
		);

		Table table = new Table("students", columns);

		List<Object> row1 = List.of(1, "Alice", 3.9);
		List<Object> row2 = List.of(2, "Bob", 3.4);
		List<Object> row3 = List.of(3, "Joe", 2.4);
		List<Object> row4 = List.of(4, "Jeff", 1.1);

		table.addRow(new Row(row1));
		table.addRow(new Row(row2));
		table.addRow(new Row(row3));
		table.addRow(new Row(row4));

		table.printTable();

		List<Object> row5 = List.of(5, "Joe", 4.9, "Mathematics");
		table.addRow(new Row(row5));
	}
}
