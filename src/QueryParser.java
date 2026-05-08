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
		return null;
	}	

	private QueryResult caseInsert(String query, Table table) {

		return null;
	}

	private QueryResult caseDelete(String query, Table table) {

		return null;
	}

	private Condition getWhereCondition(String query) {
		// condition structure: colName op value
		
		// get substring of only condition
		String conditionStr = query.substring(query.toUpperCase().indexOf("WHERE") + 6);

		// check which operator there is
		String operator;
		if (conditionStr.contains("+")) {
			operator = "+";
		} else if (conditionStr.contains(">")) {
			operator = ">";
		} else {
			operator = "<";
		}

		// get colName and value
		String[] values = conditionStr.split(operator);

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
		
		// get value from row at index
		Object rowValue = row.getValue(index);
		
		// cast condition's value to correct type based on column's DataType
		Class<?> targetType = columns.get(index).getType().getClass();
		Object conditionValueCasted = targetType.cast(condition.getValue());

		// check if rowValue and conditionValueCasted can be Compared
		if (!(rowValue instanceof Comparable && conditionValueCasted instanceof Comparable)) {
			return false;
		}

		// create Comparable version of rowValue, to be used when comparing with =, >, <
		@SuppressWarnings("unchecked")
		Comparable<Object> c1 = (Comparable<Object>) rowValue;

		
		// compare using =, >, <
		// (for STRING and BOOLEAN, only use =)
		if (targetType == Integer.class || targetType == Double.class) {
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
}
