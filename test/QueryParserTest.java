import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class QueryParserTest {

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
        students.addRow(new Row(List.of(4, "Diana", 2.8)));
        parser = new QueryParser();
    }

    // --- SELECT ---

    @Test
    void testSelectAllReturnsAllRows() {
        QueryResult result = parser.execute("SELECT * FROM students", students);
        assertEquals(4, result.getRows().size());
    }

    @Test
    void testSelectAllMessage() {
        QueryResult result = parser.execute("SELECT * FROM students", students);
        assertEquals("4 row(s) selected", result.getMessage());
    }

    @Test
    void testSelectSpecificColumns() {
        QueryResult result = parser.execute("SELECT name, gpa FROM students", students);
        assertEquals(4, result.getRows().size());
        // each returned row should only have 2 values
        assertEquals(2, result.getRows().get(0).size());
    }

    @Test
    void testSelectSpecificColumnsCorrectValues() {
        QueryResult result = parser.execute("SELECT name FROM students", students);
        assertEquals("Alice", result.getRows().get(0).getValue(0));
        assertEquals("Bob",   result.getRows().get(1).getValue(0));
    }

    @Test
    void testSelectWhereEquals() {
        QueryResult result = parser.execute("SELECT * FROM students WHERE name = Alice", students);
        assertEquals(1, result.getRows().size());
        assertEquals("Alice", result.getRows().get(0).getValue(1));
    }

    @Test
    void testSelectWhereNumericGreaterThan() {
        QueryResult result = parser.execute("SELECT * FROM students WHERE gpa > 3.5", students);
        assertEquals(2, result.getRows().size());
    }

    @Test
    void testSelectWhereNumericLessThan() {
        QueryResult result = parser.execute("SELECT * FROM students WHERE gpa < 3.0", students);
        assertEquals(1, result.getRows().size());
        assertEquals("Diana", result.getRows().get(0).getValue(1));
    }

    @Test
    void testSelectWhereIntegerEquals() {
        QueryResult result = parser.execute("SELECT * FROM students WHERE id = 3", students);
        assertEquals(1, result.getRows().size());
        assertEquals("Carol", result.getRows().get(0).getValue(1));
    }

    @Test
    void testSelectCaseInsensitiveKeywords() {
        QueryResult result = parser.execute("select * from students", students);
        assertEquals(4, result.getRows().size());
    }

    @Test
    void testSelectNoMatchReturnsEmptyList() {
        QueryResult result = parser.execute("SELECT * FROM students WHERE name = Zara", students);
        assertEquals(0, result.getRows().size());
        assertEquals("0 row(s) selected", result.getMessage());
    }

    // --- INSERT ---

    @Test
    void testInsertAddsRow() {
        parser.execute("INSERT INTO students VALUES 5, Eve, 3.2", students);
        assertEquals(5, students.getRows().size());
    }

    @Test
    void testInsertedRowHasCorrectValues() {
        parser.execute("INSERT INTO students VALUES 5, Eve, 3.2", students);
        Row inserted = students.getRows().get(4);
        assertEquals(5,     inserted.getValue(0));
        assertEquals("Eve", inserted.getValue(1));
        assertEquals(3.2,   inserted.getValue(2));
    }

    @Test
    void testInsertMessage() {
        QueryResult result = parser.execute("INSERT INTO students VALUES 5, Eve, 3.2", students);
        assertEquals("1 row(s) inserted", result.getMessage());
    }

    @Test
    void testInsertReturnsEmptyRowList() {
        QueryResult result = parser.execute("INSERT INTO students VALUES 5, Eve, 3.2", students);
        assertEquals(0, result.getRows().size());
    }

    // --- DELETE ---

    @Test
    void testDeleteWithWhereRemovesCorrectRow() {
        parser.execute("DELETE FROM students WHERE name = Bob", students);
        assertEquals(3, students.getRows().size());
    }

    @Test
    void testDeleteWithWhereMessage() {
        QueryResult result = parser.execute("DELETE FROM students WHERE name = Bob", students);
        assertEquals("1 row(s) deleted", result.getMessage());
    }

    @Test
    void testDeleteWithWhereCorrectRowRemoved() {
        parser.execute("DELETE FROM students WHERE name = Bob", students);
        for (Row row : students.getRows()) {
            assertNotEquals("Bob", row.getValue(1));
        }
    }

    @Test
    void testDeleteWithoutWhereRemovesAllRows() {
        parser.execute("DELETE FROM students", students);
        assertEquals(0, students.getRows().size());
    }

    @Test
    void testDeleteWithoutWhereMessage() {
        QueryResult result = parser.execute("DELETE FROM students", students);
        assertEquals("4 row(s) deleted", result.getMessage());
    }

    @Test
    void testDeleteReturnsEmptyRowList() {
        QueryResult result = parser.execute("DELETE FROM students WHERE id = 1", students);
        assertEquals(0, result.getRows().size());
    }

    @Test
    void testDeleteNumericWhere() {
        parser.execute("DELETE FROM students WHERE gpa < 3.5", students);
        assertEquals(2, students.getRows().size());
    }

    // --- Errors ---

    @Test
    void testMalformedQueryThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            parser.execute("EXPLODE students", students)
        );
    }

    @Test
    void testEmptyQueryThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            parser.execute("", students)
        );
    }

    @Test
    void testSelectMissingFromThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            parser.execute("SELECT * students", students)
        );
    }
}
