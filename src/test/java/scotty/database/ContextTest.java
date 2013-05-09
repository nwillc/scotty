package scotty.database;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static scotty.database.Context.superset;

public class ContextTest {
    @Test
    public void testMatch() throws Exception {
        Context context1 = new Context();
        context1.put("foo", "bar");
        context1.put("this", "that");

        assertTrue(superset(context1, context1));

        Context context2 = new Context();
        context2.put("foo", "bar");
        assertTrue(superset(context1, context2));

        context2.put("foo", "baz");
        assertFalse(superset(context1, context2));

        context2.put("foo", "bar");
        assertTrue(superset(context1, context2));

        context2.put("abe", "kobo");
        assertFalse(superset(context1, context2));
    }
}
