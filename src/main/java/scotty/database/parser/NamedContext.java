package scotty.database.parser;

import scotty.database.Context;

/**
 * A Context that has a fixed name attribute.
 */
public class NamedContext extends Context {
    private final String name;

    public NamedContext(Context parent, String name, String value) {
        super(parent);
        this.name = name;
        super.put(name, value);
    }

    public String getName() {
        return get(name);
    }

    @Override
    public boolean match(Context context) {

        if (context.containsKey(name) &&
                !getName().equals(context.get(name))) {
            return false;
        }

        return super.match(context);
    }

    @Override
    public String put(String key, String value) {
        if (getName().equals(key)) {
            throw new IllegalArgumentException("Can not change " + getName() + " name value.");
        }
        return super.put(key, value);
    }
}
