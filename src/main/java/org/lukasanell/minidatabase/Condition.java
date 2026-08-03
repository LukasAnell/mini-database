package org.lukasanell.minidatabase;

import java.util.List;

/**
 * The Condition class represents a condition used in SQL queries, such as in WHERE clauses.
 * The object represents the column name, operator, and value for a specific condition.
 *
 * Use this class to store information about a condition in a query.
 *
 * Example usage:
 * {@snippet :
 * // Example: WHERE price > 10
 * Condition condition = new Condition("price", ">", "10");
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.07
 */
public class Condition implements WhereClause {

    private String columnName;
    private String operator;
    private String value;

    /**
     * Create a Condition with the given column name, operator, and value
     *
     * @param columnName The name of the column to which the condition applies (e.g. "price")
     * @param operator The operator for the condition (e.g. ">", "{@literal <}", "=", "!=")
     * @param value The value to compare against (e.g. "10")
     */
    public Condition(String columnName, String operator, String value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Test whether the given Row satisfies this Condition
     *
     * @param row The Row to test against this Condition
     * @param columns The Table's column definitions, used to resolve the name to its index and type
     * @return true if the Row satisfies this Condition, false otherwise
     */
    @Override
    public boolean test(Row row, List<Column> columns) {
        // find which column index matches condition's colName
        int index = -1;
        for (int i = 0; i < columns.size(); i++) {
            if (this.columnName.equals(columns.get(i).getName())) {
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
        String conditionValueStr = this.value;

        // convert value to DataType
        Object conditionValueCasted = switch (type) {
            case STRING -> conditionValueStr;
            case INTEGER -> Integer.parseInt(conditionValueStr);
            case DOUBLE -> Double.parseDouble(conditionValueStr);
            case BOOLEAN -> Boolean.parseBoolean(conditionValueStr);
        };

        // check if rowValue and conditionValueCasted can be Compared
        if (
            !(
                rowValue instanceof Comparable &&
                conditionValueCasted instanceof Comparable
            )
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
            switch (this.operator) {
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
     * Get the column name for this condition
     *
     * @return the column name for this condition
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Get the operator for this condition
     *
     * @return the operator for this condition
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Get the value for this condition
     *
     * @return the value for this condition
     */
    public String getValue() {
        return value;
    }
}
