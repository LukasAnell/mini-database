import java.util.ArrayList;
import java.util.List;

public class QueryParser {
	public QueryResult execute(String query, Table table) {
		// keywords: SELECT _ FROM, WHERE, INSERT INTO, VALUES, DELETE FROM
		switch (query.split(" ")[0].toUpperCase()) {
			case "SELECT":
				return caseSelect(query, table);
			case "INSERT":
				return caseInsert(query, table);
			case "DELETE":
				return caseDelete(query, table);
			default:
				// malformed query
				throw new IllegalArgumentException();
		}
	}

	private QueryResult caseSelect(String query, Table table) {
		// save for keyword parsing
		String queryUpper = query.toUpperCase();

		// does WHERE exist?
		boolean hasWhere = queryUpper.contains("WHERE");

		if (!queryUpper.contains("FROM")) {
			throw new IllegalArgumentException();
		}

		// get column list
		// (between SELECT and FROM)
		String columnListStr = query.substring(6, queryUpper.indexOf("FROM") - 1).trim();

		String[] columnList = { "*" };
		if (!columnListStr.equals("*")) {
			columnList = columnListStr.split(", ");
		}

		// if hasWhere, turn it into a condition object
		Condition whereCondition = null;
		if (hasWhere) {
			whereCondition = getWhereCondition(query);
		}

		// loop through every row in table
		List<Row> rows = table.getRows();

		// list of collected rows
		List<Row> collectedRows = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			
			// if there's a condition, test the row against it
			boolean passes = true;
			if (whereCondition != null) {
				// test current row against condition
				passes = testRow(row, whereCondition, table.getColumns());
			}

			// if it passes, build a new Row with the requested columns' values
			// will also run with no condition
			if (passes) {
				Row collectedRow = getRequestedColumns(columnList, row, table.getColumns());
				collectedRows.add(collectedRow);
			}
		}

		// return QueryResult object
		String message = String.format("%d row(s) selected", collectedRows.size());
		return new QueryResult(collectedRows, message);
	}	

	private QueryResult caseInsert(String query, Table table) {
		String queryUpper = query.toUpperCase();

		// find where VALUES keyword is, take everything after it as arguments
		int valuesIndex = queryUpper.indexOf("VALUES");
		
		// split by ", " for each raw String value, trim whitespace
		String[] rowValues = query.substring(valuesIndex + 6).trim().split(", ");

		// use table.getColumns() to find DataType for each column,
		// then cast each value to DataType
		List<Object> values = new ArrayList<>();
		for (int i = 0; i < rowValues.length; i++) {
			DataType type = table.getColumns().get(i).getType();

			String value = rowValues[i];

			// convert value to be type
			Object convertedValue = switch (type) {
				case STRING  -> value;
				case INTEGER -> Integer.parseInt(value);
				case DOUBLE  -> Double.parseDouble(value);
				case BOOLEAN -> Boolean.parseBoolean(value);
			};

			values.add(convertedValue);
		}

		// build row from converted values
		// add to table
		table.addRow(new Row(values));
		
		// return QueryResult with an empty row list and the insert message
		String message = "1 row(s) inserted";
		return new QueryResult(new ArrayList<>(), message);
	}

	private QueryResult caseDelete(String query, Table table) {
		String queryUpper = query.toUpperCase();

		// does WHERE exist?
		boolean hasWhere = queryUpper.contains("WHERE");

		// if hasWhere, turn it into a condition object
		Condition whereCondition = null;
		if (hasWhere) {
			whereCondition = getWhereCondition(query);
		}

		// loop through every row in table
		List<Row> rows = table.getRows();

		// list of collected rows
		List<Row> collectedRows = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			
			// if there's a condition, test the row against it
			boolean passes = true;
			if (whereCondition != null) {
				// test current row against condition
				passes = testRow(row, whereCondition, table.getColumns());
			}

			// if doesn't pass, add to collectedRows
			// 
			if (!passes) {
				collectedRows.add(row);
			}
		}

		int deletedCount = table.getRows().size() - collectedRows.size();
		
		// remove matching rows from the table
		table.setRows(collectedRows);
		
		// return QueryResult object with empty row list and message saying how many removed
		String message = String.format("%d row(s) deleted", deletedCount);
		return new QueryResult(new ArrayList<>(), message);	
	}

	private Condition getWhereCondition(String query) {
		// condition structure: colName op value
		
		// get substring of only condition
		String conditionStr = query.substring(query.toUpperCase().indexOf("WHERE") + 5);

		// check which operator there is
		String operator;
		if (conditionStr.contains("=")) {
			operator = "=";
		} else if (conditionStr.contains(">")) {
			operator = ">";
		} else {
			operator = "<";
		}

		// get colName and value
		String[] values = conditionStr.split("\\" + operator);

		return new Condition(values[0].strip(), operator, values[1].strip());
	}

	private boolean testRow(Row row, Condition condition, List<Column> columns) {
		// find which column index matches condition's colName
		int index = -1;
		for (int i = 0; i < columns.size(); i++) {
			if (condition.getColumnName().equals(columns.get(i).getName())) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			throw new IllegalArgumentException();
		}
		
		// get value from row at index
		Object rowValue = row.getValue(index);

		DataType type = columns.get(index).getType();
		String conditionValueStr = condition.getValue();

		// convert value to DataType
		Object conditionValueCasted = switch (type) {
			case STRING  -> conditionValueStr;
			case INTEGER -> Integer.parseInt(conditionValueStr);
			case DOUBLE  -> Double.parseDouble(conditionValueStr);
			case BOOLEAN -> Boolean.parseBoolean(conditionValueStr);
		};

		// check if rowValue and conditionValueCasted can be Compared
		if (!(rowValue instanceof Comparable && conditionValueCasted instanceof Comparable)) {
			return false;
		}

		// create Comparable version of rowValue, to be used when comparing with =, >, <
		@SuppressWarnings("unchecked")
		Comparable<Object> c1 = (Comparable<Object>) rowValue;

		
		// compare using =, >, <
		// (for STRING and BOOLEAN, only use =)
		if (type == DataType.INTEGER || type == DataType.DOUBLE) {
			// compare with >, <
			switch (condition.getOperator()) {
				case ">":
					return c1.compareTo(conditionValueCasted) > 0;
				case "<":
					return c1.compareTo(conditionValueCasted) < 0;
			}
		}

		// compare with =
		return c1.compareTo(conditionValueCasted) == 0;
	}

	private Row getRequestedColumns(String[] requestedColumns, Row row, List<Column> columns) {
		List<Object> values = new ArrayList<>();

		// if *, return all columns
		if (requestedColumns.length == 1 && requestedColumns[0].equals("*")) {
			for (int i = 0; i < row.size(); i++) {
				values.add(row.getValue(i));
			}

			return new Row(values);
		}

		// for each requested column name, find its index in the table's column list
		for (String requestedColumn : requestedColumns) {
			for (int i = 0; i < columns.size(); i++) {
				if (requestedColumn.trim().equals(columns.get(i).getName())) {
					values.add(row.getValue(i));
				}
			}
		}

		return new Row(values);
	}
}
