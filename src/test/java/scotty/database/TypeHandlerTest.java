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
