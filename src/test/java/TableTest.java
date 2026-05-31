import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableTest {

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

    @Test
    void testTableName() {
        assertEquals("students", students.getName());
    }

    @Test
    void testColumnCount() {
        assertEquals(3, students.getColumns().size());
    }

    @Test
    void testAddAndRetrieveRow() {
        Row row = new Row(List.of(1, "Alice", 3.9));
        students.addRow(row);

        assertEquals(1, students.getRows().size());
        assertEquals(1, students.getRows().get(0).getValue(0));
        assertEquals("Alice", students.getRows().get(0).getValue(1));
    }

    @Test
    void testMalformedRowThrows() {
        Row bad = new Row(List.of(1, "Alice")); // only 2 values, needs 3
        assertThrows(IllegalArgumentException.class, () ->
            students.addRow(bad)
        );
    }

    @Test
    void testRowSize() {
        Row row = new Row(List.of(2, "Bob", 3.4));
        assertEquals(3, row.size());
    }

    @Test
    void testColumnToString() {
        Column col = new Column("age", DataType.INTEGER);
        assertEquals("age:INTEGER", col.toString());
    }
}
