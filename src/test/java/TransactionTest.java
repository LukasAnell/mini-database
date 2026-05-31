package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import main.java.Column;
import main.java.DataType;
import main.java.QueryParser;
import main.java.Row;
import main.java.Table;
import main.java.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionTest {

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

    // --- Commit ---

    @Test
    void testCommitPreservesInsert() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("INSERT INTO students VALUES 4, Diana, 2.8", parser);
        tx.commit();
        assertEquals(4, students.getRows().size());
    }

    @Test
    void testCommitPreservesDelete() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("DELETE FROM students WHERE name = Bob", parser);
        tx.commit();
        assertEquals(2, students.getRows().size());
    }

    @Test
    void testIsCommittedAfterCommit() {
        Transaction tx = new Transaction(students);
        tx.commit();
        assertTrue(tx.isCommitted());
    }

    @Test
    void testIsNotCommittedBeforeCommit() {
        Transaction tx = new Transaction(students);
        assertFalse(tx.isCommitted());
    }

    // --- Rollback ---

    @Test
    void testRollbackInsert() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("INSERT INTO students VALUES 4, Diana, 2.8", parser);
        tx.rollback();
        assertEquals(3, students.getRows().size());
    }

    @Test
    void testRollbackDelete() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("DELETE FROM students WHERE name = Bob", parser);
        tx.rollback();
        assertEquals(3, students.getRows().size());
    }

    @Test
    void testRollbackDeleteRestoresCorrectRow() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("DELETE FROM students WHERE name = Bob", parser);
        tx.rollback();
        boolean found = false;
        for (Row row : students.getRows()) {
            if (row.getValue(1).equals("Bob")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testRollbackMultipleOperations() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("INSERT INTO students VALUES 4, Diana, 2.8", parser);
        tx.executeQuery("DELETE FROM students WHERE name = Alice", parser);
        tx.rollback();
        assertEquals(3, students.getRows().size());
    }

    @Test
    void testRollbackSelectHasNoEffect() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("SELECT * FROM students", parser);
        tx.rollback();
        assertEquals(3, students.getRows().size());
    }

    @Test
    void testRollbackAfterCommitThrows() {
        Transaction tx = new Transaction(students);
        tx.executeQuery("INSERT INTO students VALUES 4, Diana, 2.8", parser);
        tx.commit();
        assertThrows(IllegalStateException.class, tx::rollback);
    }

    @Test
    void testCommitTwiceThrows() {
        Transaction tx = new Transaction(students);
        tx.commit();
        assertThrows(IllegalStateException.class, tx::commit);
    }
}
