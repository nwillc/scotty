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

package features.database;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import scotty.database.Context;
import scotty.database.Database;
import scotty.database.parser.NamedContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 *
 */
public class QueryFeature {
    private Database database;
    private Context context;
    private String value;
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

    @Given("^you find \"([^\"]*)\"$")
    public void you_find(String arg1) throws Throwable {
        value = database.find(arg1);
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

}
