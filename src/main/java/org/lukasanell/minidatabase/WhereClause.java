package org.lukasanell.minidatabase;

import java.util.List;

/**
 * A WhereClause represents a condition that can be applied to a row in a table to determine if it meets certain criteria.
 *
 * Implemented by Condition, NotCondition, and CompoundCondition classes to provide different types of filtering logic.
 * This structure is so QueryParser can treat every type of condition the same way
 *
 * Example usage:
 * {@snippet :
 * public class Condition implements WhereClause {
 *      ...
 *
 *      @Override
 *      public boolean test(Row row, List<Column> columns) {
 *          // Implementation of the test method to check if the row satisfies the condition
 *      }
 * }
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.08.03
 */
public interface WhereClause {
    /**
     * Defines a test method to be implemented by child classes,
     * which checks if a given row satisfies the condition defined by the implementing class.
     *
     * @param row The row to be tested against the condition
     * @param columns The list of columns in the table, used to access the values in the row
     * @return true if the row satisfies the condition, false otherwise
     */
    boolean test(Row row, List<Column> columns);
}
