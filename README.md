# mini-database

---

## Project Summary

This is a small project that has served as practice implementing a relational database engine in Java. My goal with this was to support a few simple types, data persistence with CSV files, a few simple SQL-like queries, hash and tree indexing, and transaction management with rollbacks.

---

## Features

- Typed columns (`INTEGER`, `DOUBLE`, `STRING`, `BOOLEAN`)
- CSV saving and loading
- SQL-like queries including: `SELECT`, `INSERT`, `DELETE`, with optional `WHERE` clauses
- Hash indexing for fast equality lookups
- Tree indexing for sorted and range-based lookups
- Transactions that support commits and rollbacks

## Project Structure

```
❯ tree --dirsfirst
.
├── lib
│   └── junit-platform-console-standalone-1.10.2.jar
├── src
│   ├── Column.java             # Column name and type definition
│   ├── Condition.java          # Represents a WHERE clause condition
│   ├── CsvStorage.java         # Save and load tables as CSV files
│   ├── DataType.java           # Enum of currently supported column types
│   ├── HashIndex.java          # HashMap-based index for equality lookups
│   ├── QueryParser.java        # Parses and executes queries
│   ├── QueryResult.java        # Object to store rows returned and a result message
│   ├── Row.java                # Represents a single row of typed values
│   ├── Table.java              # In-memory table of rows and columns
│   ├── Transaction.java        # Used for executing queries with rollback support
│   └── TreeIndex.java          # TreeMap-based index for range lookups
├── test                        # Testing files
│   ├── resources
│   │   └── test_students.csv   # Example .csv file used in tests
│   ├── CsvStorageTest.java
│   ├── IndexTest.java
│   ├── QueryParserTest.java
│   ├── TableTest.java
│   └── TransactionTest.java
├── README.md
└── pom.xml
```

---

## How to Build and Run

### Manual

**Compile sources:**
```bash
javac -cp lib/junit-platform-console-standalone-1.10.2.jar src/*.java -d bin/
```

**Compile tests:**
```bash
javac -cp lib/junit-platform-console-standalone-1.10.2.jar:bin/ test/*.java -d bin/
```

**Run tests:**
```bash
java -jar lib/junit-platform-console-standalone-1.10.2.jar --class-path bin/ --scan-class-path
```

---

### With Maven (recommended)

**Compile sources:**
```bash
mvn compile
```

**Run tests:**
```bash
mvn test
```

**Run the main class:**
```bash
mvn exec:java -Dexec.mainClass="Main"
```

---

## Query Syntax

### SELECT
```sql
SELECT * FROM tableName
SELECT col1, col2 FROM tableName
SELECT * FROM tableName WHERE column = value
SELECT * FROM tableName WHERE column > value
SELECT * FROM tableName WHERE column < value
```

### INSERT
```sql
INSERT INTO tableName VALUES val1, val2, val3
```

### DELETE
```sql
DELETE FROM tableName
DELETE FROM tableName WHERE column = value
```

**Notes:**
- Keywords are case-insensitive, but values are case-sensitive
- `WHERE` supports `=`, `<`, `>`
    - Numeric comparisons can be used for `INTEGER` and `DOUBLE`
    - For `STRING` and `BOOLEAN`, only equality can be used
- Column values must not contain commas

---

## Design Decisions

**Snapshot-based undo log for Transaction** \
Initially, I had logic to store inverse queries as Strings or some other operation that would reverse the given operation. However, my implementation was way too complex (in my opinion), so the current implementation simply captures a snapshot of the table's row list before a given change as a `Runnable` lambda. When a rollback is called, they're replayed in reverse order. I think this simpler, but much more memory intensive implementation is much easier for now and doesn't rely on the query parser.

**Two index types for quick access** \
`HashIndex` uses a `HashMap`, so it should ideally be used for exact-match lookups in O(1) average time. `TreeIndex` uses a `TreeMap` with a `Comparable` based comparator, which keeps key entries in it sorted. Using TreeMap as an index type allows for range queries on the data with `subMap`. Both index types are built eagerly at construction time.

**`QueryParser` is stateless** \
`QueryParser` doesn't retain any data about a Table or its data. Its `execute` method takes in both the query string and the target table as arguments, which means it's safe to reuse a single Parser object across multiple tables and transactions.

**CSV for persistent storage** \
I thought a CSV would be the best for this because it's a simple plaintext representation of the database. The first line of each CSV file encodes the column names and types (e.g. `id:INTEGER,name:STRING`), which means the in-memory database schema can be easily reconstructed on load without having to store any other metadata.

---

## Limitations

- No `UPDATE` query
- `WHERE` clauses currently only support a single condition
    - No `AND`, `OR`, or `NOT`
- Values in queries and in CSV files cannot contain commas
- Indexes will not be written to disk for persistence, and must be rebuilt after loading a table from a CSV
- Column types are not enforced at insert time, and will result in a runtime parsing error
- No support for `NULL` values

---

## (Possible) Future Updates

- Add `UPDATE` query support, along with undo logic in Transaction
- Support compound `WHERE` conditions with `AND` and `OR`
    - `NOT` support also included
- Persist each index type alongside the CSV files so they can survive restarts
- Add an interactive shell so the database can be queried from the command line
- Enforce types on `INSERT`, along with meaningful error messages
- Support multiple tables with `JOIN` queries
