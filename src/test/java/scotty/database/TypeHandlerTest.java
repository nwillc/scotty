package scotty.database;

import org.junit.Before;
import org.junit.Test;
import scotty.database.parser.TypeHandler;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class TypeHandlerTest {
    private Type type;

    @Before
    public void before() throws Exception {
        type = TypeHandler.parse("./target/test-classes/host.xml");
        assertNotNull(type);
    }

    @Test
    public void parse() throws Exception {
        assertEquals("host", type.getName());
        assertNotNull(type.get("address"));
        assertEquals("127.0.0.1", type.get("address"));

        Instance instance = (Instance) type.getContained().get("devbox2");
        assertEquals("dev", instance.get("env"));
        assertEquals("192.0.0.2", instance.get("address"));
        assertEquals("acme", instance.get("company"));

        instance = (Instance) type.getContained().get("prod1");
        assertEquals("prod", instance.get("env"));
        assertEquals("192.0.0.3", instance.get("address"));
        assertEquals("acme", instance.get("company"));
    }


}
