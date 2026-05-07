import java.util.List;

public class Row {
	private List<Object> values;

	public Row(List<Object> values) {
		this.values = values;
	}

	public Object getValue(int index) {
		return values.get(index);
	}

	public int size() {
		return values.size();
	}

	@Override
	public String toString() {
		return values.toString();
	}
}
