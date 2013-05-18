/*
 * Copyright (c) 2013, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or
 * without fee is hereby granted, provided that the above copyright notice and this permission
 * notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO
 * THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT
 * SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR
 * ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
 * CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */

package scotty.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Context is a set of attributes, that, can also inherit attributes from its parent.
 */
public class Context implements Comparable<Context> {
	private static int next = 0;
	private final Map<String, Value> map = new HashMap<>();
	private final Context container;
	private final Map<String, Context> contained = new HashMap<>();
	private final int age;

	public Context() {
		this(null, null);
	}

	public Context(Context container) {
		this.container = container;
		this.age = Context.nextAge();
	}

	public Context(String assignmentList) {
		this(null, assignmentList);
	}

	public Context(Context container, String assignmentList) {
		this(container);

		if (assignmentList == null) {
			return;
		}

		final String[] assignments = assignmentList.trim().split(",");
		for (String assignment : assignments) {
			String[] labelValue = assignment.split("=");
			if (labelValue.length != 2) {
				continue;
			}
			put(labelValue[0].trim(), labelValue[1].trim());
		}
	}

	public final Map<String, Value> getMap() {
		return map;
	}

	public final Map<String, Context> getContained() {
		return contained;
	}

	public final Context getContainer() {
		return container;
	}

	public void put(String key, Value value) {
		map.put(key, value);
	}


	public void put(String key, String value) {
		map.put(key, new Value(value));
	}

	public Set<String> keySet() {
		Set<String> keys = new HashSet<>(getMap().keySet());

		if (isContained()) {
			keys.addAll(getContainer().keySet());
		}

		return keys;
	}

	public boolean containsKey(String key) {
		return keySet().contains(key);
	}

	public Value getValue(String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}

		if (isContained()) {
			return container.getValue(key);
		}

		return null;
	}

	public String get(String key) {
		Value value = getValue(key);
		return value == null ? null : value.toString();
	}

	public boolean isContained() {
		return container != null;
	}

	public boolean isContainer() {
		return contained.size() > 0;
	}

	public int getAge() {
		return age;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
		sb.append("{");
		sb.append("Map{");

		boolean first = true;
		for (String key : getMap().keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(key);
			sb.append("=");
			sb.append(getMap().get(key));
		}

		sb.append("}");
		sb.append(", isContained=");
		sb.append(isContained());
		if (!isContainer()) {
			sb.append(", isContainer=");
			sb.append(isContainer());
		} else {
			sb.append(", Contained{");
			first = true;
			for (String key : getContained().keySet()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(key);
				sb.append("=");
				sb.append(getContained().get(key));
			}
			sb.append("}");
		}
		sb.append(", age=").append(age);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public int compareTo(Context o) {
		return age - o.age;
	}

	private synchronized static int nextAge() {
		return ++next;
	}
}
