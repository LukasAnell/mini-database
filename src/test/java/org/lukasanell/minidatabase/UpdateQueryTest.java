package org.lukasanell.minidatabase;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateQueryTest {

    private Table students;
    private QueryParser parser;

    @BeforeEach
    void setUp() {
        List<Column> columns = List.of(
            new Column("id", DataType.INTEGER),
            new Column("name", DataType.STRING),
            new Column("gpa", DataType.DOUBLE)
        );
        students = new Table("students", columns);
        students.addRow(new Row(List.of(1, "Alice", 3.9)));
        students.addRow(new Row(List.of(2, "Bob", 3.4)));
        students.addRow(new Row(List.of(3, "Carol", 3.7)));
        parser = new QueryParser();
    }

    // --- UPDATE basic ---

    @Test
    void testUpdateSingleRow() {
        parser.execute("UPDATE students SET gpa = 4.0 WHERE id = 1", students);
        assertEquals(4.0, students.getRows().get(0).getValue(2));
    }

    @Test
    void testUpdateMessage() {
        QueryResult result = parser.execute(
            "UPDATE students SET gpa = 4.0 WHERE id = 1",
            students
        );
        assertEquals("1 row(s) updated", result.getMessage());
    }

    @Test
    void testUpdateAllRows() {
        parser.execute("UPDATE students SET gpa = 1.0", students);
        for (Row row : students.getRows()) {
            assertEquals(1.0, row.getValue(2));
        }
    }

    @Test
    void testUpdateAllRowsMessage() {
        QueryResult result = parser.execute(
            "UPDATE students SET gpa = 1.0",
            students
        );
        assertEquals("3 row(s) updated", result.getMessage());
    }

    @Test
    void testUpdateStringColumn() {
        parser.execute("UPDATE students SET name = Eve WHERE id = 2", students);
        assertEquals("Eve", students.getRows().get(1).getValue(1));
    }

    @Test
    void testUpdateNoMatchingRows() {
        QueryResult result = parser.execute(
            "UPDATE students SET gpa = 4.0 WHERE id = 99",
            students
        );
        assertEquals("0 row(s) updated", result.getMessage());
    }

    @Test
    void testUpdateReturnsEmptyRowList() {
        QueryResult result = parser.execute(
            "UPDATE students SET gpa = 4.0 WHERE id = 1",
            students
        );
        assertEquals(0, result.getRows().size());
    }

    @Test
    void testUpdateTypeMismatchThrows() {
        assertThrows(TypeMismatchException.class, () ->
            parser.execute(
                "UPDATE students SET gpa = notadouble WHERE id = 1",
                students
            )
        );
    }

    @Test
    void testUpdateInvalidColumnThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            parser.execute(
                "UPDATE students SET score = 4.0 WHERE id = 1",
                students
            )
        );
    }

    @Test
    void testUpdateDoesNotAffectOtherRows() {
        parser.execute("UPDATE students SET gpa = 4.0 WHERE id = 1", students);
        assertEquals(3.4, students.getRows().get(1).getValue(2));
        assertEquals(3.7, students.getRows().get(2).getValue(2));
    }

    // --- UPDATE + Transaction ---

    @Test
    void testUpdateRollback() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("UPDATE students SET gpa = 4.0 WHERE id = 1", parser);
        tx.rollback();
        assertEquals(3.9, students.getRows().get(0).getValue(2));
    }

    @Test
    void testUpdateRollbackOnlyAffectedRows() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("UPDATE students SET gpa = 4.0 WHERE id = 1", parser);
        tx.rollback();
        assertEquals(3.4, students.getRows().get(1).getValue(2));
        assertEquals(3.7, students.getRows().get(2).getValue(2));
    }

    @Test
    void testUpdateCommitPersists() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("UPDATE students SET gpa = 4.0 WHERE id = 1", parser);
        tx.commit();
        assertEquals(4.0, students.getRows().get(0).getValue(2));
    }

    @Test
    void testUpdateRollbackMultipleRows() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("UPDATE students SET gpa = 1.0", parser);
        tx.rollback();
        assertEquals(3.9, students.getRows().get(0).getValue(2));
        assertEquals(3.4, students.getRows().get(1).getValue(2));
        assertEquals(3.7, students.getRows().get(2).getValue(2));
    }
}
