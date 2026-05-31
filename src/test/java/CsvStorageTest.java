package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import main.java.Column;
import main.java.CsvStorage;
import main.java.DataType;
import main.java.Row;
import main.java.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CsvStorageTest {

    private static final String TEST_FILE = "../resources/test_students.csv";
    private Table original;

    @BeforeEach
    void setUp() {
        List<Column> columns = List.of(
            new Column("id", DataType.INTEGER),
            new Column("name", DataType.STRING),
            new Column("gpa", DataType.DOUBLE)
        );
        original = new Table("students", columns);
        original.addRow(new Row(List.of(1, "Alice", 3.9)));
        original.addRow(new Row(List.of(2, "Bob", 3.4)));
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE).delete();
    }

    @Test
    void testSaveAndLoadTableName() throws IOException {
        CsvStorage.saveTable(original, TEST_FILE);
        Table loaded = CsvStorage.loadTable("students", TEST_FILE);
        assertEquals("students", loaded.getName());
    }

    @Test
    void testSaveAndLoadColumnCount() throws IOException {
        CsvStorage.saveTable(original, TEST_FILE);
        Table loaded = CsvStorage.loadTable("students", TEST_FILE);
        assertEquals(3, loaded.getColumns().size());
    }

    @Test
    void testSaveAndLoadColumnTypes() throws IOException {
        CsvStorage.saveTable(original, TEST_FILE);
        Table loaded = CsvStorage.loadTable("students", TEST_FILE);
        assertEquals(DataType.INTEGER, loaded.getColumns().get(0).getType());
        assertEquals(DataType.STRING, loaded.getColumns().get(1).getType());
        assertEquals(DataType.DOUBLE, loaded.getColumns().get(2).getType());
    }

    @Test
    void testSaveAndLoadRowCount() throws IOException {
        CsvStorage.saveTable(original, TEST_FILE);
        Table loaded = CsvStorage.loadTable("students", TEST_FILE);
        assertEquals(2, loaded.getRows().size());
    }

    @Test
    void testLoadedValuesHaveCorrectTypes() throws IOException {
        CsvStorage.saveTable(original, TEST_FILE);
        Table loaded = CsvStorage.loadTable("students", TEST_FILE);
        Object id = loaded.getRows().get(0).getValue(0);
        Object gpa = loaded.getRows().get(0).getValue(2);
        assertInstanceOf(Integer.class, id);
        assertInstanceOf(Double.class, gpa);
    }

    @Test
    void testLoadedValuesAreCorrect() throws IOException {
        CsvStorage.saveTable(original, TEST_FILE);
        Table loaded = CsvStorage.loadTable("students", TEST_FILE);
        assertEquals(1, loaded.getRows().get(0).getValue(0));
        assertEquals("Alice", loaded.getRows().get(0).getValue(1));
        assertEquals(3.9, loaded.getRows().get(0).getValue(2));
    }

    @Test
    void testLoadNonExistentFileThrows() {
        assertThrows(IOException.class, () ->
            CsvStorage.loadTable("ghost", "nonexistent_file.csv")
        );
    }

    @Test
    void testBooleanColumnSavesAndLoads() throws IOException {
        List<Column> cols = List.of(
            new Column("name", DataType.STRING),
            new Column("enrolled", DataType.BOOLEAN)
        );
        Table boolTable = new Table("enrollment", cols);
        boolTable.addRow(new Row(List.of("Alice", true)));
        boolTable.addRow(new Row(List.of("Bob", false)));

        CsvStorage.saveTable(boolTable, TEST_FILE);
        Table loaded = CsvStorage.loadTable("enrollment", TEST_FILE);

        assertInstanceOf(Boolean.class, loaded.getRows().get(0).getValue(1));
        assertEquals(true, loaded.getRows().get(0).getValue(1));
        assertEquals(false, loaded.getRows().get(1).getValue(1));
    }
}
