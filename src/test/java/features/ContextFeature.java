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

package features;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import scotty.database.Context;

import java.util.Set;

import static junit.framework.TestCase.*;

/**
 *
 */
public class ContextFeature {
    private Context context;
    private String value;

    @Given("^a Context instantiated with \"([^\"]*)\"$")
    public void a_Context_instantiated_with(String arg1) throws Throwable {
        context = new Context(null, arg1);
        assertNotNull(context);
    }

    @When("^you search for \"([^\"]*)\"$")
    public void you_search_for(String arg1) throws Throwable {
        value = context.get(arg1);
        assertNotNull(value);
    }

    @Then("^it value should be \"([^\"]*)\"$")
    public void it_value_should_be(String arg1) throws Throwable {
        assertEquals(arg1, value);
    }

    @Given("^a its child instantiated with \"([^\"]*)\"$")
    public void a_its_child_instantiated_with(String arg1) throws Throwable {
        Context child = new Context(context, arg1);
        assertNotNull(child);
        context = child;
    }

    @Then("^the keySet should contain \"([^\"]*)\"$")
    public void the_keySet_should_contain(String arg1) throws Throwable {
        String[] keys = arg1.split(",");
        Set<String> keySet = context.keySet();
        for (String key : keys) {
            assertTrue(keySet.contains(key));
        }
    }

}
