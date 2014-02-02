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
import cucumber.api.java.en.When;
import scotty.database.Context;
import scotty.database.Database;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

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
		assertThat(database).isNotNull();
    }

    @Then("^you should receive a Database with (\\d+) Types$")
    public void you_should_receive_a_Database_with_Types(int arg1) throws Throwable {
		assertThat(database.getContained().size()).isEqualTo(arg1);
    }

    @Then("^it should contain types \"([^\\\"]*)\"$")
    public void it_should_contain_type_host(List<String> types) throws Throwable {
        for (String typeName : types) {
			assertThat(database.getContained().containsKey(typeName)).isTrue();
        }
    }

    @Then("^the value of \"([^\"]*)\" \"([^\"]*)\" should be \"([^\"]*)\"$")
    public void the_value_of_should_be(String arg1, String arg2, String arg3) throws Throwable {
        List<Context> matches = database.query(new Context(arg1));
		assertThat(matches).isNotNull();
		assertThat(matches.size()).isNotZero();
        String value = matches.get(0).get(arg2);
		assertThat(value).isEqualTo(arg3);
    }

}
