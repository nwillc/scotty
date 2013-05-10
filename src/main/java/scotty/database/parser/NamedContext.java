package scotty.database.parser;

import scotty.database.Context;

/**
 * A Context that has a fixed name attribute.
 */
public abstract class NamedContext extends Context {

    protected NamedContext(Context parentContext, String name) {
        super(parentContext);
        put(getElementType(), name);
    }

    protected abstract String getElementType();

    public String getName() {
        return get(getElementType());
    }

}
