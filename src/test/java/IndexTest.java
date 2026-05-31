package java;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IndexTest {

    private Table students;

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
        students.addRow(new Row(List.of(5, "Eve", 3.4)));
    }

    // --- HashIndex ---

    @Test
    void testHashIndexLookupFindsRow() {
        HashIndex index = new HashIndex("id", students);
        List<Row> result = index.lookup(1);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getValue(0));
    }

    @Test
    void testHashIndexLookupMultipleRows() {
        HashIndex index = new HashIndex("gpa", students);
        List<Row> result = index.lookup(3.4);
        assertEquals(2, result.size());
    }

    @Test
    void testHashIndexLookupMissReturnsEmpty() {
        HashIndex index = new HashIndex("id", students);
        List<Row> result = index.lookup(99);
        assertEquals(0, result.size());
    }

    @Test
    void testHashIndexInsert() {
        HashIndex index = new HashIndex("id", students);
        Row newRow = new Row(List.of(6, "Frank", 3.1));
        index.insert(6, newRow);
        assertEquals(1, index.lookup(6).size());
    }

    @Test
    void testHashIndexRemove() {
        HashIndex index = new HashIndex("id", students);
        Row target = students.getRows().get(0);
        index.remove(1, target);
        assertEquals(0, index.lookup(1).size());
    }

    @Test
    void testHashIndexColumnName() {
        HashIndex index = new HashIndex("name", students);
        assertEquals("name", index.getColumnName());
    }

    // --- TreeIndex ---

    @Test
    void testTreeIndexLookupFindsRow() {
        TreeIndex index = new TreeIndex("id", students);
        List<Row> result = index.lookup(1);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getValue(0));
    }

    @Test
    void testTreeIndexLookupMissReturnsEmpty() {
        TreeIndex index = new TreeIndex("id", students);
        List<Row> result = index.lookup(99);
        assertEquals(0, result.size());
    }

    @Test
    void testTreeIndexRangeLookupInclusive() {
        TreeIndex index = new TreeIndex("gpa", students);
        List<Row> result = index.lookupRange(3.4, 3.9);
        assertEquals(4, result.size());
    }

    @Test
    void testTreeIndexRangeLookupNarrow() {
        TreeIndex index = new TreeIndex("gpa", students);
        List<Row> result = index.lookupRange(3.8, 4.0);
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getValue(1));
    }

    @Test
    void testTreeIndexInsert() {
        TreeIndex index = new TreeIndex("id", students);
        Row newRow = new Row(List.of(6, "Frank", 3.1));
        index.insert(6, newRow);
        assertEquals(1, index.lookup(6).size());
    }

    @Test
    void testTreeIndexRemove() {
        TreeIndex index = new TreeIndex("id", students);
        Row target = students.getRows().get(0);
        index.remove(1, target);
        assertEquals(0, index.lookup(1).size());
    }

    @Test
    void testTreeIndexColumnName() {
        TreeIndex index = new TreeIndex("gpa", students);
        assertEquals("gpa", index.getColumnName());
    }
}
