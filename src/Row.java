/**
 *
 * @author LukasAnell
 * @version 1.0
 * @since 2026.05.06
 */
import java.util.List;

public class Row {

    private List<Object> values;

    /**
     *
     * @param values
     */
    public Row(List<Object> values) {
        this.values = values;
    }

    /**
     *
     * @param index
     * @return
     */
    public Object getValue(int index) {
        return values.get(index);
    }

    /**
     *
     * @return
     */
    public int size() {
        return values.size();
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i < values.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
