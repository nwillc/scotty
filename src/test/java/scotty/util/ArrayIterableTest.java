/*
 * Copyright (c) 2013-2014, nwillc@gmail.com
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

package scotty.util;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.Assert.*;
import static org.junit.Assert.assertNotNull;
import static scotty.util.ArrayIterable.newArrayIterable;

public class ArrayIterableTest {

	@Test
	public void shouldHandleNullArray() throws Exception {
		Iterable<String> strings = new ArrayIterable<>(null);

		assertNotNull(strings);
		Iterator<String> iter = strings.iterator();
		assertNotNull(iter);
		assertFalse(iter.hasNext());
	}

	@Test
	public void shouldIterateAll() throws Exception {
		String[] stringArray = new String[]{"a", "b", "c"};
		final List<String> stringList = newArrayList(stringArray);

		int length = 0;
		for (String s : newArrayIterable(stringArray)) {
			assertTrue(stringList.contains(s));
			stringList.remove(s);
			length++;
		}
		assertEquals(stringArray.length, length);
	}
}
