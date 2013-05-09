package scotty.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A Context is a set of attributes, that, can also inherit attributes from its parent.
 */
public class Context extends HashMap<String, String> {
    private static final Logger LOGGER = Logger.getLogger(Context.class.getName());
    private final Context parent;

    public Context() {
        this(null);
    }

    public Context(Context parent) {
        this.parent = parent;
    }

    /**
     * Checks if A is a superset of B. A must have all the attributes, with equal values of B.
     */
    public static boolean superset(Context a, Context b) {
        if (b == null) {
            return true;
        }

        for (String key : b.keySet()) {
            String value = a.get(key);
            if (!b.get(key).equals(value)) {
                return false;
            }
        }

        return true;
    }

    /**
     * The similarity score is the count of all matching attributes between two contexts. If any attribute name is shared where
     * the values differ the similarity is -1;
     */
    public static int similarity(Context a, Context b) {
        int score = 0;
        if (a == null || b == null) {
            return score;
        }

        for (String key : b.keySet()) {
            if (!a.containsKey(key)) {
                continue;
            }

            if (!a.get(key).equals(b.get(key))) {
                return -1;
            }
            score++;
        }
        return score;
    }

    public String query(String attributeName, Context context) {
        if (superset(this, context)) {
            return get(attributeName);
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return keySet().contains((String) key);
    }

    @Override
    public Set<String> keySet() {
        Set<String> allkeys = new HashSet<>();
        allkeys.addAll(super.keySet());
        if (parent != null) {
            allkeys.addAll(parent.keySet());
        }

        return allkeys;
    }

    @Override
    public String get(Object name) {
        if (super.containsKey(name)) {
            return super.get(name);
        }

        if (parent != null) {
            return parent.get(name);
        }

        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Context{");
        sb.append("parent=").append(parent);
        for (String key : keySet()) {
            sb.append(", ");
            sb.append(key);
            sb.append("=");
            sb.append(get(key));
        }
        sb.append('}');
        return sb.toString();
    }
}
