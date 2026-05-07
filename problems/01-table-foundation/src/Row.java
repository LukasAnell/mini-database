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
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			sb.append(values.get(i));
			if (i < values.size() - 1) sb.append(", ");
		}
		return sb.toString();
	}
}
