/**
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2025.05.06
 */
public class Column {

    private String name;
    private DataType type;

    /**
     *
     * @param name
     * @param type
     */
    public Column(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public DataType getType() {
        return type;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return name + ":" + type;
    }
}
