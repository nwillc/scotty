package scotty.database;

import org.xml.sax.SAXException;
import scotty.database.parser.TypeHandler;
import scotty.database.parser.Utilities;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * A map of Types.
 */
public class Database extends Context {

    public static Database parse(String... files) throws ParserConfigurationException, SAXException, IOException {
        if (files == null || files.length == 0) {
            return null;
        }

        Database database = new Database();
        for (String file : files) {
            Type type = TypeHandler.parse(file);
            database.getChildren().put(type.getName(), type);
        }
        return database;
    }

    public String find(String name) {
        return Utilities.find(this, name);
    }

    public List<Context> query(Context criteria) {
        return Utilities.query(this, criteria);
    }

    public String match(String name, Context criteria) {
        List<Context> contexts = query(criteria);

        if (contexts != null && contexts.size() > 0) {
            return contexts.get(0).get(name);
        }

        return null;
    }
}
