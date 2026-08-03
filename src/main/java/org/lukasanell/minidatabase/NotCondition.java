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
     *
     * @param condition
     */
    public NotCondition(WhereClause condition) {
        this.condition = condition;
    }

    /**
     */
    @Override
    public boolean test(Row row, List<Column> columns) {
        return !condition.test(row, columns);
    }

    /**
     *
     * @return
     */
    public WhereClause getCondition() {
        return condition;
    }

    /**
     *
     * @param condition
     */
    public void setCondition(WhereClause condition) {
        this.condition = condition;
    }
}
