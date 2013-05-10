package scotty.database;

import scotty.database.parser.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A type in a SCoTTY database.
 */
public class Type extends NamedContext implements SubContexts<Instance>, Retrieval<Instance>, InstanceQuery {
    private final Map<String, Instance> instances = new HashMap<>();

    public Type(String name) {
        super(null, name);
    }

    public String query(String attributeName, Context context) {
        Instance instance = find(context);
        return instance == null ? null : instance.attr(attributeName);
    }

    @Override
    public Map<String, Instance> getMap() {
        return instances;
    }

    @Override
    protected String getElementType() {
        return Elements.TYPE;
    }

    @Override
    public Instance get(String label) {
        return instances.get(label);
    }

    @Override
    public String attr(String label) {
        String[] part = label.split("\\.");
        Instance instance = get(part[0]);
        return instance == null ? null : instance.attr(part[1]);
    }

    @Override
    public Instance find(Context context) {
        return Utilities.find(this, context);
    }

    @Override
    public List<Instance> match(Context context) {
        return Utilities.match(this, context);
    }

}
