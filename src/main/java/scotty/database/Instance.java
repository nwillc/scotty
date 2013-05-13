package scotty.database;

import scotty.database.parser.Elements;
import scotty.database.parser.NamedContext;


/**
 * An instance of a Type.
 */
public class Instance extends NamedContext {

    public Instance(Context parentContext, String name) {
        super(parentContext, name);
    }

    @Override
    protected String getElementType() {
        return Elements.INSTANCE;
    }

}
