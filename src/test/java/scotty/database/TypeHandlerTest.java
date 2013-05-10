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
        assertNotNull(type.getContext().get("address"));
        assertEquals("127.0.0.1", type.getContext().get("address"));

        Instance instance = type.getMap().get("devbox2");
        assertEquals("dev", instance.getContext().get("env"));
        assertEquals("192.0.0.2", instance.getContext().get("address"));
        assertEquals("acme", instance.getContext().get("company"));

        instance = type.getMap().get("prod1");
        assertEquals("prod", instance.getContext().get("env"));
        assertEquals("192.0.0.3", instance.getContext().get("address"));
        assertEquals("acme", instance.getContext().get("company"));
    }


}
