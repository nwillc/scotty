package scotty.database;

import org.xml.sax.SAXException;
import scotty.database.parser.Elements;
import scotty.database.parser.Retrieval;
import scotty.database.parser.TypeHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  A map of Types.
 */
public class Database implements Retrieval<Type> {
    private final Map<String,Type> types = new HashMap<>();

	public static Database parse(String ... files) throws ParserConfigurationException, SAXException, IOException {
		if (files == null || files.length == 0) {
			return null;
		}

		Database database = new Database();
		for (String file : files) {
			Type type = TypeHandler.parse(file);
			database.getTypes().put(type.getName(), type);
		}
		return database;
	}

    public String query(String attributeName, Context context) {

        // Optimization, if a type is specified reduce the search space to that type
        if (context.containsKey(Elements.TYPE)) {
            String name = context.get(Elements.TYPE);
            Type type = get(name);
            return  type == null ? null : type.query(attributeName, context);
        }

        for (Type type : types.values()) {
            String value = type.query(attributeName, context);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public Map<String, Type> getTypes() {
        return types;
    }

    @Override
    public Type get(String label) {
        return types.get(label);
    }

    @Override
    public String attr(String label) {
        String[] part = label.split("\\.");
        Type type = get(part[0]);
        if (type == null) {
            return null;
        }
        Instance instance = type.get(part[1]);
        if (instance == null) {
            return null;
        }
        return instance.get(part[2]);
    }
}
