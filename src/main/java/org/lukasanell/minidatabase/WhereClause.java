package org.lukasanell.minidatabase;

import java.util.List;

/**
 *
 */
public interface WhereClause {
    boolean test(Row row, List<Column> columns);
}
