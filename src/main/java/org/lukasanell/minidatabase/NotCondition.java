package org.lukasanell.minidatabase;

import java.util.List;

/**
 * The NotCondition class represents a negation of a condition used in SQL queries, such as in WHERE clauses.
 * It implements the WhereClause interface and negates the result of another condition.
 *
 * Use this class to represent a NOT condition in a query.
 *
 * Example usage:
 * {@snippet :
 * // Example: WHERE NOT (price > 10)
 * Condition condition = new Condition("price", ">", "10");
 * NotCondition notCondition = new NotCondition(condition);
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.08.03
 */
public class NotCondition implements WhereClause {

    private WhereClause condition;

    /**
     * Creates a NotCondition that negates the given condition.
     *
     * @param condition The condition to be negated
     */
    public NotCondition(WhereClause condition) {
        this.condition = condition;
    }

    /**
     * Tests whether the given Row satisfies the negated condition.
     *
     * @param row The Row to test against the negated condition
     * @param columns The Table's column definitions, used to resolve the name to its index
     * @return true if the Row does not satisfy the original condition, false otherwise
     */
    @Override
    public boolean test(Row row, List<Column> columns) {
        return !condition.test(row, columns);
    }

    /**
     * Get the condition that is being negated.
     *
     * @return the condition that is being negated
     */
    public WhereClause getCondition() {
        return condition;
    }
}
