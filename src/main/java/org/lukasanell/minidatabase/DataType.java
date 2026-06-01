package org.lukasanell.minidatabase;

/**
 * The DataType enum represents the different data types that can be used in the database
 *
 * The supported data types are:
 * - INTEGER: represents whole numbers (e.g., 1, 42, -5)
 * - DOUBLE: represents floating-point numbers (e.g., 3.14, -0)
 * - STRING: represents sequences of characters (e.g., "Hello", "World")
 * - BOOLEAN: represents true/false values (e.g., true, false)
 *
 * Example usage:
 * {@snippet :
 * DataType type = DataType.INTEGER;
 * if (type == DataType.INTEGER) {
 *     System.out.println("The data type is INTEGER");
 * }
 * }
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.06
 */
public enum DataType {
    /**
     * Represents whole numbers (e.g., 1, 42, -5)
     */
    INTEGER,

    /**
     * Represents floating-point numbers (e.g., 3.14, -0)
     */
    DOUBLE,

    /**
     * Represents sequences of characters (e.g., "Hello", "World")
     */
    STRING,

    /**
     * Represents true/false values (e.g., true, false)
     */
    BOOLEAN,
}
