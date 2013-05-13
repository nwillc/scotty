package scotty.database;

import scotty.database.parser.Elements;
import scotty.database.parser.NamedContext;

/**
 * A type in a SCoTTY database.
 */
public class Type extends NamedContext {

    public Type(String name) {
        super(null, name);
    }

    @Override
    protected String getElementType() {
        return Elements.TYPE;
    }

}
