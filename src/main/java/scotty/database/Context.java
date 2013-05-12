package scotty.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Context is a set of attributes, that, can also inherit attributes from its parent.
 */
public class Context {
	private final Map<String, String> map = new HashMap<>();
	private final Map<String, Context> children = new HashMap<>();
	private final Context parent;

	public Context() {
		this(null);
	}

	public Context(Context parent) {
		this.parent = parent;
	}

	public Context(Context parent, String assignmentList) {
		this(parent);

		final String[] assignments = assignmentList.trim().split(",");
		for (String assignment : assignments) {
			String[] labelValue = assignment.split("=");
			put(labelValue[0].trim(), labelValue[1].trim());
		}
	}

	public final Map<String, String> getMap() {
		return map;
	}

	public final Map<String, Context> getChildren() {
		return children;
	}

	public final Context getParent() {
		return parent;
	}

	public void put(String key, String value) {
		map.put(key, value);
	}

	public Set<String> keySet() {
		Set<String> keys = new HashSet<>(getMap().keySet());

		if (isChild()) {
			keys.addAll(getParent().keySet());
		}

		return keys;
	}

	public boolean containsKey(String key) {
		return keySet().contains(key);
	}

	public String get(String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}

		if (isChild()) {
			return parent.get(key);
		}

		return null;
	}

	public boolean isChild() {
		return parent != null;
	}

	public boolean isParent() {
		return children.size() > 0;
	}
}
