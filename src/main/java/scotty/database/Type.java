package scotty.database;

import scotty.database.parser.Elements;
import scotty.database.parser.NamedContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A type in a SCoTTY database.
 */
public class Type extends NamedContext {
    private final Map<String, Instance> instances = new HashMap<>();

    public Type(String name) {
        super(null, name);
    }

    @Override
    protected String getElementType() {
        return Elements.TYPE;
    }


}
