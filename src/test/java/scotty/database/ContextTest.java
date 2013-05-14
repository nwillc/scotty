/*
 * Copyright (c) 2013, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or
 * without fee is hereby granted, provided that the above copyright notice and this permission
 * notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO
 * THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT
 * SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR
 * ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
 * CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */

package scotty.database;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static scotty.database.parser.Utilities.superset;

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

		System.out.println(context2.toString());
	}
}
