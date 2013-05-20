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

import scotty.database.parser.Similarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Context is a set of attributes, that can inherit attributes from its parent and contain sub-contexts within it.
 */
public class Context implements Comparable<Context>, Similarity<Context> {
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

    /**
     * Get the map of key/Values attributes of this context.
     *
     * @return the attribute map.
     */
    public final Map<String, Value> getMap() {
        return map;
    }

    /**
     * Get the map of sub-contexts withing this context.
     *
     * @return the map of contained contexts.
     */
    public final Map<String, Context> getContained() {
        return contained;
    }

    /**
     * Get the context this context is contained in.
     *
     * @return the containing context.
     */
    public final Context getContainer() {
        return container;
    }

    /**
     * Put a key/Value pair into this context.
     *
     * @param key   the key
     * @param value the value
     */
    public void put(String key, Value value) {
        map.put(key, value);
    }


    /**
     * Add a key/string to the context as a key/Value.
     *
     * @param key   the key
     * @param value the string value
     */
    public void put(String key, String value) {
        map.put(key, new Value(value));
    }

    /**
     * Get the key set of this context, including those inherited from the containing context.
     *
     * @return the key set
     */
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>(getMap().keySet());

        if (isContained()) {
            keys.addAll(getContainer().keySet());
        }

        return keys;
    }

    /**
     * Check if this context, or its containing contexts, contains a key.
     *
     * @param key the key
     * @return true if the key set contains the key
     */
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

    /**
     * Get the string value of a Value associated with a given key.
     *
     * @param key key
     * @return string
     */
    public String get(String key) {
        Value value = getValue(key);
        return value == null ? null : value.toString();
    }

    /**
     * Is this context contained in another?
     *
     * @return true if context is contained in another.
     */
    public boolean isContained() {
        return container != null;
    }

    /**
     * Does this context contain sub contexts?
     *
     * @return true if this context contains sub contexts.
     */
    public boolean isContainer() {
        return contained.size() > 0;
    }

    /**
     * Get the "age" of this context. Each instance of Context is given an increasing "age" number for use in ranking.
     *
     * @return the age
     */
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

    @Override
    public float similarity(Context b) {
        float score = NOT_SIMILAR;

        if (b == null) {
            return score;
        }

        for (String key : b.keySet()) {
            if (!containsKey(key)) {
                continue;
            }

            float vScore = getValue(key).similarity(b.getValue(key));
            if (vScore == NOT_SIMILAR) {
                return NOT_SIMILAR;
            }
            score += vScore;
        }
        return score;
    }

    private synchronized static int nextAge() {
        return ++next;
    }
}
