package org.lukasanell.minidatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that parses and executes SQL-like queries on a given Table object
 *
 * Use the execute method to run a query on a Table.
 * The execute method returns a QueryResult object, which contains the rows resulting from the query and a message about the query execution.
 *
 * Supported query formats:
 * - SELECT column1, column2 FROM tableName WHERE column3 > 5
 * - SELECT * FROM tableName
 * - INSERT INTO tableName VALUES value1, value2, value3
 * - DELETE FROM tableName
 * - DELETE FROM tableName WHERE column1 = 'value'
 *
 * Example usage:
 * {@snippet :
 * // Assume `table` is an instance of Table, and is already created and populated
 * QueryParser parser = new QueryParser();
 * QueryResult result = parser.execute("SELECT name, price FROM products WHERE price > 10", table);
 *
 * System.out.println(result.getMessage());
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.07
 */
public class QueryParser {

    /**
     * Create a QueryParser object.
     * This constructor is intentionally empty because there is no initialization needed for this class.
     */
    public QueryParser() {
        // intentionally empty
    }

    /**
     * Parse the query string and execute it on the given Table object, returning a QueryResult with the results and a message.
     *
     * @param query The SQL-like query string to be executed
     * @param table The Table object the query is executed on
     * @return a QueryResult object containing the resulting rows and a message about the query execution
     */
    public QueryResult execute(String query, Table table) {
        switch (query.split(" ")[0].toUpperCase()) {
            case "SELECT":
                return caseSelect(query, table);
            case "INSERT":
                return caseInsert(query, table);
            case "DELETE":
                return caseDelete(query, table);
            case "UPDATE":
                return caseUpdate(query, table);
            default:
                // malformed query
                throw new IllegalArgumentException();
        }
    }

    /**
     * Handle SELECT queries, both with or without WHERE conditions.
     *
     * @param query The full SELECT query string to be executed
     * @param table The Table object the query is executed on
     * @return a QueryResult object
     */
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
        String columnListStr = query
            .substring(6, queryUpper.indexOf("FROM") - 1)
            .trim();

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
                Row collectedRow = getRequestedColumns(
                    columnList,
                    row,
                    table.getColumns()
                );
                collectedRows.add(collectedRow);
            }
        }

        // return QueryResult object
        String message = String.format(
            "%d row(s) selected",
            collectedRows.size()
        );
        return new QueryResult(collectedRows, message);
    }

    /**
     * Handle INSERT queries
     *
     * @param query The full INSERT query string to be executed
     * @param table The Table object the query is executed on
     * @return a QueryResult object
     */
    private QueryResult caseInsert(String query, Table table) {
        String queryUpper = query.toUpperCase();

        // find where VALUES keyword is, take everything after it as arguments
        int valuesIndex = queryUpper.indexOf("VALUES");

        // split by ", " for each raw String value, trim whitespace
        String[] rowValues = query
            .substring(valuesIndex + 6)
            .trim()
            .split(", ");

        // use table.getColumns() to find DataType for each column,
        // then cast each value to DataType
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < rowValues.length; i++) {
            DataType type = table.getColumns().get(i).getType();

            String value = rowValues[i];

            // convert value to be type
            Object convertedValue = switch (type) {
                case STRING -> value;
                case INTEGER -> {
                    try {
                        yield Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new TypeMismatchException(
                            table.getColumns().get(i).getName(),
                            type,
                            value
                        );
                    }
                }
                case DOUBLE -> {
                    try {
                        yield Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        throw new TypeMismatchException(
                            table.getColumns().get(i).getName(),
                            type,
                            value
                        );
                    }
                }
                case BOOLEAN -> {
                    if (
                        value.equalsIgnoreCase("true") ||
                        value.equalsIgnoreCase("false")
                    ) {
                        yield Boolean.parseBoolean(value);
                    } else {
                        throw new TypeMismatchException(
                            table.getColumns().get(i).getName(),
                            type,
                            value
                        );
                    }
                }
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

    /**
     * Handle DELETE queries, both with or without WHERE conditions.
     *
     * @param query The full DELETE query string to be executed
     * @param table The Table object the query is executed on
     * @return a QueryResult object
     */
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

    /**
     *
     * @param query
     * @param table
     * @return
     */
    private QueryResult caseUpdate(String query, Table table) {
        String queryUpper = query.toUpperCase();

        boolean hasWhere = queryUpper.contains("WHERE");

        Condition whereCondition = null;
        if (hasWhere) {
            whereCondition = getWhereCondition(query);
        }

        List<Row> rows = table.getRows();

        // get column name and value to set from query
        // (between SET and WHERE, or between SET and end of query if no WHERE)
        String setStr = hasWhere
            ? query
                  .substring(
                      queryUpper.indexOf("SET") + 3,
                      queryUpper.indexOf("WHERE")
                  )
                  .trim()
            : query.substring(queryUpper.indexOf("SET") + 3).trim();

        String[] setParts = setStr.split("=");
        if (setParts.length != 2) {
            throw new IllegalArgumentException();
        }

        String setColumnName = setParts[0].trim();
        String setValueStr = setParts[1].trim();

        // find column index and type for setColumnName
        int setColumnIndex = -1;
        DataType setColumnType = null;
        for (int i = 0; i < table.getColumns().size(); i++) {
            if (table.getColumns().get(i).getName().equals(setColumnName)) {
                setColumnIndex = i;
                setColumnType = table.getColumns().get(i).getType();

                break;
            }
        }

        if (setColumnIndex == -1) {
            throw new IllegalArgumentException();
        }

        // convert setValueStr to setColumnType
        Object setValue = switch (setColumnType) {
            case STRING -> setValueStr;
            case INTEGER -> {
                try {
                    yield Integer.parseInt(setValueStr);
                } catch (NumberFormatException e) {
                    throw new TypeMismatchException(
                        setColumnName,
                        setColumnType,
                        setValueStr
                    );
                }
            }
            case DOUBLE -> {
                try {
                    yield Double.parseDouble(setValueStr);
                } catch (NumberFormatException e) {
                    throw new TypeMismatchException(
                        setColumnName,
                        setColumnType,
                        setValueStr
                    );
                }
            }
            case BOOLEAN -> {
                if (
                    setValueStr.equalsIgnoreCase("true") ||
                    setValueStr.equalsIgnoreCase("false")
                ) {
                    yield Boolean.parseBoolean(setValueStr);
                } else {
                    throw new TypeMismatchException(
                        setColumnName,
                        setColumnType,
                        setValueStr
                    );
                }
            }
        };

        List<Row> updatedRows = new ArrayList<>();
        for (Row row : rows) {
            boolean passes = true;

            if (whereCondition != null) {
                passes = testRow(row, whereCondition, table.getColumns());
            }

            if (passes) {
                // create new row with same values as current row, but with setColumnIndex value changed to setValue
                List<Object> newRowValues = new ArrayList<>();
                for (int i = 0; i < row.size(); i++) {
                    if (i == setColumnIndex) {
                        newRowValues.add(setValue);
                    } else {
                        newRowValues.add(row.getValue(i));
                    }
                }

                updatedRows.add(new Row(newRowValues));
            } else {
                updatedRows.add(row);
            }
        }

        int updatedCount = 0;
        for (int i = 0; i < rows.size(); i++) {
            if (!rows.get(i).equals(updatedRows.get(i))) {
                updatedCount++;
            }

            rows.set(i, updatedRows.get(i));
        }

        String message = String.format("%d row(s) updated", updatedCount);

        return new QueryResult(new ArrayList<>(), message);
    }

    /**
     * Parse the WHERE condition from the query string and return it as a Condition object.
     *
     * @param query The full query string containing the WHERE condition
     * @return a Condition object representing the parsed WHERE condition
     */
    private Condition getWhereCondition(String query) {
        // condition structure: colName op value

        // get substring of only condition
        String conditionStr = query.substring(
            query.toUpperCase().indexOf("WHERE") + 5
        );

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

    /**
     * Test if a given Row satisfies a given Condition, based on the column definitions in the table.
     *
     * @param row The Row to be tested against the condition
     * @param condition The Condition object representing the condition to test against
     * @param columns The list of Column objects representing the table's column definitions (used to determine data types and column indices)
     * @return true if the Row satisfies the Condition, false otherwise
     */
    private boolean testRow(
        Row row,
        Condition condition,
        List<Column> columns
    ) {
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
            case STRING -> conditionValueStr;
            case INTEGER -> Integer.parseInt(conditionValueStr);
            case DOUBLE -> Double.parseDouble(conditionValueStr);
            case BOOLEAN -> Boolean.parseBoolean(conditionValueStr);
        };

        // check if rowValue and conditionValueCasted can be Compared
        if (
            !(rowValue instanceof Comparable &&
                conditionValueCasted instanceof Comparable)
        ) {
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

    /**
     * Given a Row and a list of requested column names, return a new Row containing only the values of the requested columns.
     *
     * @param requestedColumns An array of column names that were requested in the SELECT query (or "*" for all columns)
     * @param row The Row object from which to extract the requested column values
     * @param columns The list of Column objects representing the table's column definitions (used to determine column indices)
     * @return a new Row object containing only the values of the requested columns from the input Row
     */
    private Row getRequestedColumns(
        String[] requestedColumns,
        Row row,
        List<Column> columns
    ) {
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
