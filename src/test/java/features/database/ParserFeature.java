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

import static junit.framework.TestCase.*;

/**
 *
 */
public class ParserFeature {
    private String[] filename;
    private Database database;

    @Given("^the XML \"([^\\\"]*)\"$")
    public void the_XML_target_test_classes_xml(List<String> filenames) throws Throwable {
        this.filename = filenames.toArray(new String[0]);
    }

    @When("^parsed by the Database class$")
    public void parsed_by_the_Database_class() throws Throwable {
        database = Database.parse(filename);
        assertNotNull(database);
    }

    @Then("^you should receive a Database with (\\d+) Types$")
    public void you_should_receive_a_Database_with_Types(int arg1) throws Throwable {
        assertEquals(arg1, database.getContained().size());
    }

    @Then("^it should contain types \"([^\\\"]*)\"$")
    public void it_should_contain_type_host(List<String> types) throws Throwable {
        for (String typeName : types) {
            assertTrue(database.getContained().containsKey(typeName));
        }
    }
}
