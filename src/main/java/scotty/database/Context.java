package scotty.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *  A Context is a set of attributes, that, can also inherit attributes from its parent.
 */
public class Context extends HashMap<String, String> {
    private static final Logger LOGGER = Logger.getLogger(Context.class.getName());
    private static boolean VERBOSE = false;
    private final Context parent;

	public Context() {
		this(null);
	}

	public Context(Context parent) {
		this.parent = parent;
	}


    public static boolean isVerbose() {
        return VERBOSE;
    }

    public static void setVerbose(boolean VERBOSE) {
        Context.VERBOSE = VERBOSE;
    }


    public boolean match(Context context) {
		if (context == null) {
			return false;
		}

		for (String key : context.keySet()) {
			String value = get(key);
            if (!context.get(key).equals(value)) {
                return false;
            }
		}

        if (VERBOSE) {
            LOGGER.info("Match: \n" + toString() + "\n" + context.toString());
        }
		return true;
	}

    public String query(String attributeName, Context context) {
        if (match(context)) {
            return get(attributeName);
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return keySet().contains((String)key);
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
