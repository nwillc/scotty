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

package scotty.database.parser;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class RankedTest {
	@Test
	public void sortTest() throws Exception {
		List<Ranked<Integer>> list = new LinkedList<>();

		list.add(new Ranked<>(1.0f, 0, 1));
		list.add(new Ranked<>(3.0f, 1, 4));
		list.add(new Ranked<>(3.0f, 0, 3));
		list.add(new Ranked<>(2.0f, 0, 2));

		Collections.sort(list);

		assertThat(list.get(0).getData().intValue()).isEqualTo(3);
		assertThat(list.get(1).getData().intValue()).isEqualTo(4);
		assertThat(list.get(2).getData().intValue()).isEqualTo(2);
		assertThat(list.get(3).getData().intValue()).isEqualTo(1);

		list.add(new Ranked<>(3.0f, -1, 0));

		Collections.sort(list);

		assertThat(list.get(0).getData().intValue()).isEqualTo(0);
	}

}
