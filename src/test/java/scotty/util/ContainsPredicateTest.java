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

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.api.Assertions.assertThat;
import static scotty.util.ContainsPredicate.newContainsPredicate;
import static scotty.util.Iterables.forEach;

public class ContainsPredicateTest {
	private Collection<String> strings;

	@Before
	public void setUp() throws Exception {
		strings = newArrayList("a", "b", "c");
	}

	@Test
	public void testContained() throws Exception {
		forEach(strings, new Consumer<String>() {
			@Override
			public void accept(String str) {
				assertThat(newContainsPredicate(strings).apply(str)).isTrue();
			}
		});
	}

	@Test
	public void testNotContained() throws Exception {
		assertThat(newContainsPredicate(strings).apply("d")).isFalse();
	}
}