package scotty.database;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ContextTest {
	@Test
	public void testMatch() throws Exception {
		Context context1 = new Context();
		context1.put("foo","bar");
		context1.put("this", "that");

		assertTrue(context1.match(context1));

		Context context2 = new Context();
		context2.put("foo", "bar");
		assertTrue(context1.match(context2));

		context2.put("foo","baz");
		assertFalse(context1.match(context2));

		context2.put("foo","bar");
		assertTrue(context1.match(context2));

		context2.put("abe", "kobo");
		assertFalse(context1.match(context2));
	}
}
