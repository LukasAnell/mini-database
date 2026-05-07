import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvStorage {
	public static void saveTable(Table table, String filePath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			List<Column> columns = table.getColumns();
			// loop through columns, .toString() for each one
			for (int i = 0; i < columns.size(); i++) {
				writer.write(columns.get(i).toString());
				if (i < columns.size() - 1) {
					writer.write(",");
				}
			}
			writer.newLine();

			List<Row> rows = table.getRows();
			for (int i = 0; i < rows.size(); i++) {
				Row currentRow = rows.get(i);
				for (int j = 0; j < currentRow.size(); j++) {
					Object value = currentRow.getValue(j);

					writer.write(value.toString());
					if (i < currentRow.size() - 1) {
						writer.write(",");
					}
				}

				writer.newLine();
			}
		} catch (IOException e) {
			throw e;
		}
	}

	public static Table loadTable(String tableName, String filePath) {

	}
}
