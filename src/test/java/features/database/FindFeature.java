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
import cucumber.api.java.en.When;
import scotty.database.Database;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 *
 */
public class FindFeature {
    private Database database;
    private String value;

    @Given("^a Database loaded from \"([^\"]*)\"$")
    public void a_Database_loaded_from(List<String> filenames) throws Throwable {
        database = Database.parse(filenames.toArray(new String[0]));
        assertNotNull(database);
    }

    @When("^the value of \"([^\"]*)\" is retreived$")
    public void the_value_of_is_retreived(String arg1) throws Throwable {
        value = database.find(arg1);
        assertNotNull(value);
    }

    @Then("^it should equal \"([^\"]*)\"$")
    public void it_should_equal(String arg1) throws Throwable {
        assertEquals(arg1, value);
    }

}
