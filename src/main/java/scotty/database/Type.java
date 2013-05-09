package scotty.database;

import scotty.database.parser.Elements;
import scotty.database.parser.NamedContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  A type in a SCoTTY database.
 */
public class Type extends NamedContext {
	private final Map<String, Instance> instances = new HashMap<>();

	public Type(String name) {
		super(null, Elements.TYPE, name);
	}

	public void add(Instance instance) {
		instances.put(instance.getName(), instance);
	}

	public Map<String, Instance> getInstances() {
		return instances;
	}

    @Override
    public String query(String attributeName, Context context) {

        if (context.containsKey(Elements.INSTANCE)) {
            String iName = context.get(Elements.INSTANCE);
            Instance instance = getInstances().get(iName);
            return  instance == null ? null : instance.query(attributeName, context);
        }

        for (Instance instance : getInstances().values()) {
            String value = instance.query(attributeName, context);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
