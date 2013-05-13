package scotty.database;

import org.xml.sax.SAXException;
import scotty.database.parser.TypeHandler;
import scotty.database.parser.Utilities;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A map of Types.
 */
public class Database extends Context {

    public static Database parse(InputStream... inputStreams) throws IOException, SAXException, ParserConfigurationException {
        Database database = new Database();

        if (inputStreams != null) {
            for (InputStream inputStream : inputStreams) {
                Type type = TypeHandler.parse(inputStream);
                database.getContained().put(type.getName(), type);
            }
        }
        return database;
    }

    public static Database parse(String... files) throws ParserConfigurationException, SAXException, IOException {
        if (files == null || files.length == 0) {
            return null;
        }

        Database database = new Database();
        for (String file : files) {
            Type type = TypeHandler.parse(file);
            database.getContained().put(type.getName(), type);
        }
        return database;
    }

    public String find(String name) {
        return Utilities.find(this, name);
    }

    public List<Context> query(Context criteria) {
        return Utilities.query(this, criteria);
    }

}
