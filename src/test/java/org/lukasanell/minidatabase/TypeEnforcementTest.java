package org.lukasanell.minidatabase;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeEnforcementTest {

    private Table students;

    @BeforeEach
    void setUp() {
        List<Column> columns = List.of(
            new Column("id", DataType.INTEGER),
            new Column("name", DataType.STRING),
            new Column("gpa", DataType.DOUBLE)
        );
        students = new Table("students", columns);
    }

    // --- TypeMismatchException ---

    @Test
    void testExceptionMessage() {
        TypeMismatchException e = new TypeMismatchException(
            "gpa",
            DataType.DOUBLE,
            "notanumber"
        );
        assertTrue(e.getMessage().contains("gpa"));
        assertTrue(e.getMessage().contains("DOUBLE"));
        assertTrue(e.getMessage().contains("notanumber"));
    }

    @Test
    void testExceptionGetters() {
        TypeMismatchException e = new TypeMismatchException(
            "id",
            DataType.INTEGER,
            "abc"
        );
        assertEquals("id", e.getColumnName());
        assertEquals(DataType.INTEGER, e.getExpectedType());
        assertEquals("abc", e.getActualValue());
    }

    // --- Table type enforcement ---

    @Test
    void testValidRowInserts() {
        assertDoesNotThrow(() ->
            students.addRow(new Row(List.of(1, "Alice", 3.9)))
        );
    }

    @Test
    void testIntegerColumnRejectsString() {
        assertThrows(TypeMismatchException.class, () ->
            students.addRow(new Row(List.of("notanint", "Alice", 3.9)))
        );
    }

    @Test
    void testDoubleColumnRejectsString() {
        assertThrows(TypeMismatchException.class, () ->
            students.addRow(new Row(List.of(1, "Alice", "notadouble")))
        );
    }

    @Test
    void testStringColumnRejectsInteger() {
        assertThrows(TypeMismatchException.class, () ->
            students.addRow(new Row(List.of(1, 42, 3.9)))
        );
    }

    @Test
    void testBooleanColumnRejectsString() {
        List<Column> cols = List.of(
            new Column("name", DataType.STRING),
            new Column("enrolled", DataType.BOOLEAN)
        );
        Table t = new Table("enrollment", cols);
        assertThrows(TypeMismatchException.class, () ->
            t.addRow(new Row(List.of("Alice", "notabool")))
        );
    }

    @Test
    void testDoubleColumnAcceptsInteger() {
        assertDoesNotThrow(() ->
            students.addRow(new Row(List.of(1, "Alice", 3)))
        );
    }

    @Test
    void testInvalidRowNotInserted() {
        try {
            students.addRow(new Row(List.of("notanint", "Alice", 3.9)));
        } catch (TypeMismatchException e) {
            // expected
        }
        assertEquals(0, students.getRows().size());
    }

    // --- QueryParser type enforcement ---

    @Test
    void testInsertQueryRejectsBadInteger() {
        QueryParser parser = new QueryParser();
        assertThrows(TypeMismatchException.class, () ->
            parser.execute(
                "INSERT INTO students VALUES notanint, Alice, 3.9",
                students
            )
        );
    }

    @Test
    void testInsertQueryRejectsBadDouble() {
        QueryParser parser = new QueryParser();
        assertThrows(TypeMismatchException.class, () ->
            parser.execute(
                "INSERT INTO students VALUES 1, Alice, notadouble",
                students
            )
        );
    }

    @Test
    void testInsertQueryValidData() {
        QueryParser parser = new QueryParser();
        assertDoesNotThrow(() ->
            parser.execute(
                "INSERT INTO students VALUES 1, Alice, 3.9",
                students
            )
        );
    }
}
