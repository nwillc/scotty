package scotty.database;

import scotty.database.parser.Elements;
import scotty.database.parser.NamedContext;
import scotty.database.parser.Retrieval;

import java.util.HashMap;
import java.util.Map;

/**
 * A type in a SCoTTY database.
 */
public class Type extends NamedContext implements Retrieval<Instance> {
    private final Map<String, Instance> instances = new HashMap<>();

    public Type(String name) {
        super(null, name);
    }

    public Map<String, Instance> getInstances() {
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

    public String query(String attributeName, Context context) {

        if (context.containsKey(Elements.INSTANCE)) {
            String iName = context.get(Elements.INSTANCE);
            Instance instance = getInstances().get(iName);
            return instance == null ? null : instance.getContext().query(attributeName, context);
        }

        for (Instance instance : getInstances().values()) {
            String value = instance.getContext().query(attributeName, context);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
