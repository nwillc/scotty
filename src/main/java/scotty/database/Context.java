package scotty.database;

import scotty.database.parser.Utilities;

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

    public String query(String attributeName, Context context) {
        if (Utilities.superset(this, context)) {
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
