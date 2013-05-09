package scotty.database;

import scotty.database.parser.Elements;
import scotty.database.parser.NamedContext;
import scotty.database.parser.Retrieval;

/**
 * An instance of a Type.
 */
public class Instance extends NamedContext implements Retrieval<String> {

	public Instance(Context parentContext, String name) {
       super(parentContext, name);
	}

    @Override
    protected String getElementType() {
        return Elements.INSTANCE;
    }

    @Override
    public String get(String label) {
        return attr(label);
    }

    @Override
    public String attr(String label) {
        return getContext().get(label);
    }
}
