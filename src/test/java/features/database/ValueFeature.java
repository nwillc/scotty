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
import scotty.database.Value;

import static org.fest.assertions.api.Assertions.assertThat;

public class ValueFeature {
	private Value value;
	private String string;
	private boolean match;

	@Given("^a value of \"([^\"]*)\"$")
	public void a_value_of(String arg1) throws Throwable {
		String[] values = arg1.split(" ");
		value = new Value(values);
		assertThat(value).isNotNull();
		assertThat(value.values().size()).isGreaterThan(0);
	}

	@Given("^a \"([^\"]*)\"$")
	public void a(String arg1) throws Throwable {
		string = arg1;
	}

	@When("^they are matched$")
	public void they_are_matched() throws Throwable {
		match = value.values().contains(string);
	}

	@Then("^if should return \"([^\"]*)\"$")
	public void if_should_return(String arg1) throws Throwable {
		assertThat(String.valueOf(match)).isEqualTo(arg1);
	}

	@Then("^its string representation should be \"([^\"]*)\"$")
	public void its_string_representation_should_be(String arg1) throws Throwable {
		assertThat(value.toString()).isEqualTo(arg1);
	}


}
