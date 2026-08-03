package org.lukasanell.minidatabase;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WhereClauseTest {

    private Table students;
    private QueryParser parser;

    @BeforeEach
    void setUp() {
        List<Column> columns = List.of(
            new Column("id", DataType.INTEGER),
            new Column("name", DataType.STRING),
            new Column("gpa", DataType.DOUBLE),
            new Column("active", DataType.BOOLEAN),
            new Column("notes", DataType.STRING)
        );
        students = new Table("students", columns);
        students.addRow(new Row(List.of(1, "Alice", 3.9, true, "honors")));
        students.addRow(new Row(List.of(2, "Bob", 3.4, true, "probation")));
        students.addRow(new Row(List.of(3, "Carol", 3.7, false, "honors")));
        students.addRow(new Row(List.of(4, "Dave", 2.9, false, "probation")));
        parser = new QueryParser();
    }

    // --- Regression: single condition still works through the new WhereClause path ---

    @Test
    void testSingleConditionSelectStillWorks() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE gpa > 3.5",
            students
        );
        assertEquals(2, result.getRows().size());
    }

    @Test
    void testSingleConditionDeleteStillWorks() {
        parser.execute("DELETE FROM students WHERE id = 4", students);
        assertEquals(3, students.getRows().size());
    }

    @Test
    void testSingleConditionUpdateStillWorks() {
        parser.execute("UPDATE students SET gpa = 4.0 WHERE id = 1", students);
        assertEquals(4.0, students.getRows().get(0).getValue(2));
    }

    // --- AND chains ---

    @Test
    void testAndTwoConditions() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE gpa > 3.5 AND active = true",
            students
        );
        // only Alice matches both
        assertEquals(1, result.getRows().size());
    }

    @Test
    void testAndThreeConditions() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE gpa > 2.5 AND active = false AND notes = honors",
            students
        );
        // only Carol matches all three
        assertEquals(1, result.getRows().size());
    }

    @Test
    void testAndExcludesNonMatchingRows() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE gpa > 3.8 AND active = false",
            students
        );
        // nobody has gpa > 3.8 AND active = false (Carol is 3.7, Dave is 2.9)
        assertEquals(0, result.getRows().size());
    }

    // --- OR chains ---

    @Test
    void testOrTwoConditions() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE id = 1 OR id = 4",
            students
        );
        assertEquals(2, result.getRows().size());
    }

    @Test
    void testOrMatchesEitherSide() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE gpa > 3.8 OR active = false",
            students
        );
        // Alice (gpa > 3.8), Carol and Dave (active = false)
        assertEquals(3, result.getRows().size());
    }

    // --- NOT ---

    @Test
    void testNotSingleCondition() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE NOT active = true",
            students
        );
        // Carol and Dave are not active
        assertEquals(2, result.getRows().size());
    }

    @Test
    void testNotCombinedWithAnd() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE NOT active = true AND notes = probation",
            students
        );
        // only Dave: not active AND on probation
        assertEquals(1, result.getRows().size());
    }

    @Test
    void testNotCombinedWithOr() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE NOT active = true OR gpa > 3.8",
            students
        );
        // Carol, Dave (not active) + Alice (gpa > 3.8)
        assertEquals(3, result.getRows().size());
    }

    // --- Mixed AND/OR rejection ---

    @Test
    void testMixedAndOrThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            parser.execute(
                "SELECT * FROM students WHERE gpa > 3.5 AND active = true OR id = 4",
                students
            )
        );
    }

    // --- Word-boundary safety ---
    // A column literally named "notes" must not be misread as the NOT keyword.

    @Test
    void testColumnNamedNotesIsNotMisparsedAsNotKeyword() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE notes = honors",
            students
        );
        assertEquals(2, result.getRows().size());
    }

    @Test
    void testColumnNamedNotesWorksInAndChain() {
        QueryResult result = parser.execute(
            "SELECT * FROM students WHERE notes = honors AND active = true",
            students
        );
        // only Alice: honors AND active
        assertEquals(1, result.getRows().size());
    }
}
