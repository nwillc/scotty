package scotty.template;

import org.junit.Test;
import scotty.database.Database;

import java.io.FileInputStream;

/**
 *
 */
public class ParserTest {

    @Test
    public void parse() throws Exception {
        Database database = Database.parse("./target/test-classes/host.xml");
        try (FileInputStream fileInputStream = new FileInputStream("./target/test-classes/prop-template.scotty") ) {
            Parser.parse(fileInputStream, System.out, database, null);
        }
    }
}
