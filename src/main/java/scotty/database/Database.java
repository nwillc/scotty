package scotty.database;

import org.xml.sax.SAXException;
import scotty.database.parser.Elements;
import scotty.database.parser.TypeHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

/**
 *  A map of Types.
 */
public class Database extends HashMap<String,Type> {

	public static Database parse(String ... files) throws ParserConfigurationException, SAXException, IOException {
		if (files == null || files.length == 0) {
			return null;
		}

		Database database = new Database();
		for (String file : files) {
			Type type = TypeHandler.parse(file);
			database.add(type);
		}
		return database;
	}

	public void add(Type type) {
		this.put(type.getName(), type);
	}

    public String query(String attributeName, Context context) {

        // Optimization, if a type is specified reduce the search space to that type
        if (context.containsKey(Elements.TYPE)) {
            String name = context.get(Elements.TYPE);
            Type type = get(name);
            return  type == null ? null : type.query(attributeName, context);
        }

        for (Type type : values()) {
            String value = type.query(attributeName, context);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public String getAttribute(String attribute) {
        String[] parts = attribute.split("\\.");

        Type type = get(parts[0]);
        Instance instance = type.getInstances().get(parts[1]);
        return instance.get(parts[2]);
    }
}
