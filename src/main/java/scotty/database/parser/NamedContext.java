package scotty.database.parser;

import scotty.database.Context;

/**
 * A Context that has a fixed name attribute.
 */
public abstract class NamedContext  {
    private final Context context;

	public NamedContext(Context parentContext, String name) {
        this.context = new Context(parentContext);
        context.put(getElementType(), name);
	}

    protected abstract String getElementType();

    public Context getContext() {
        return context;
    }

    public String getName() {
        return context.get(getElementType());
    }

}
