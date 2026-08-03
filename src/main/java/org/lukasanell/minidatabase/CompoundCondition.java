package org.lukasanell.minidatabase;

import java.util.List;

/**
 * The CompoundCondition class represents a compound condition used in SQL queries, such as in WHERE clauses.
 * It implements the WhereClause interface and combines two conditions with a logical operator (AND or OR).
 *
 * Use this class to represent a compound condition in a query.
 *
 * Example usage:
 * {@snippet :
 * // Example: WHERE (price > 10) AND (quantity < 5)
 * Condition condition1 = new Condition("price", ">", "10");
 * Condition condition2 = new Condition("quantity", "<", "5");
 * CompoundCondition compoundCondition = new CompoundCondition(condition1, "AND", condition2);
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.08.03
 */
public class CompoundCondition implements WhereClause {

    private WhereClause left;
    private String operator;
    private WhereClause right;

    /**
     * Creates a CompoundCondition that combines two conditions with a logical operator (AND or OR).
     *
     * @param left The left condition to be combined
     * @param operator The logical operator to combine the conditions (AND or OR)
     * @param right The right condition to be combined
     */
    public CompoundCondition(
        WhereClause left,
        String operator,
        WhereClause right
    ) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * Tests whether the given Row satisfies the compound condition.
     *
     * @param row The Row to test against the compound condition
     * @param columns The Table's column definitions, used to resolve the name to its index
     * @return true if the Row satisfies the compound condition, false otherwise
     */
    @Override
    public boolean test(Row row, List<Column> columns) {
        switch (operator) {
            case "AND":
                return left.test(row, columns) && right.test(row, columns);
            case "OR":
                return left.test(row, columns) || right.test(row, columns);
            default:
                throw new IllegalArgumentException(
                    "Invalid operator: " + operator
                );
        }
    }

    /**
     * Gets the left condition of the compound condition.
     *
     * @return the left condition
     */
    public WhereClause getLeft() {
        return left;
    }

    /**
     * Gets the logical operator of the compound condition (AND or OR).
     *
     * @return the logical operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Gets the right condition of the compound condition.
     *
     * @return the right condition
     */
    public WhereClause getRight() {
        return right;
    }
}
