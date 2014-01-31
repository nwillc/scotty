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

package features.database;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import scotty.database.Context;
import scotty.database.Database;
import scotty.database.parser.NamedContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;

/**
 *
 */
public class QueryFeature {
	private Database database;
	private Context context;
	private String value;
	private String attribute;
	private List<Context> results;

	@Given("^a Database based on$")
	public void a_Database_based_on(String arg1) throws Throwable {
		try (InputStream inputStream = new ByteArrayInputStream(arg1.getBytes())) {
			database = Database.parse(inputStream);
		}
		assertTrue(database.getContained().size() > 0);
	}

	@Given("^the database$")
	public void the_database() throws Throwable {
		assertNotNull(database);
	}

    @Given("^you find \"([^\"]*)\" \"([^\"]*)\"$")
    public void you_find(String arg1, String arg2) throws Throwable {
        List<Context> matches = database.query(new Context(arg1));
        assertNotNull(matches);
        assertNotEquals(0,matches.size());
		value = matches.get(0).get(arg2);
		assertNotNull(value);
	}

	@Then("^it should return \"([^\"]*)\"$")
	public void it_should_return(String arg1) throws Throwable {
		assertEquals(value, arg1);
	}

	@Given("^the context based on \"([^\"]*)\"$")
	public void the_context_based_on(String arg1) throws Throwable {
		context = new Context(null, arg1);
		assertNotNull(context);
	}

	@Then("^query should yield (\\d+) instances$")
	public void query_should_yield_instances(int arg1) throws Throwable {
		results = database.query(context);
		assertEquals(arg1, results.size());
	}

	@Then("^the first instance should be \"([^\"]*)\"$")
	public void the_first_instance_should_be(String arg1) throws Throwable {
		if (results.size() > 0) {
			NamedContext namedContext = (NamedContext) results.get(0);
			assertEquals(arg1, namedContext.getName());
		} else {
			assertEquals(0, arg1.length());
		}
	}

	@Given("^and the attribute \"([^\"]*)\"$")
	public void and_the(String arg1) throws Throwable {
		attribute = arg1;
	}

	@Then("^it should yield set \"([^\"]*)\"$")
	public void it_should_set(String arg1) throws Throwable {
		Set<String> set = database.query(context, attribute);

		String[] values = arg1.split(",");

		assertEquals(values.length, set.size());
		for (String value : values) {
			assertTrue(set.contains(value));
		}
	}

}
