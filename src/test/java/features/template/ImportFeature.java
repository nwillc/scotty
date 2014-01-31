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

package features.template;

import com.google.common.base.Optional;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import scotty.database.Context;
import scotty.database.Database;
import scotty.template.NamedScriptEngine;
import scotty.template.Parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static junit.framework.TestCase.assertEquals;
import static scotty.ScottyUtilities.getResourceAsStream;

/**
 *
 */
public class ImportFeature {
    private InputStream inputStream;
    private OutputStream outputStream;

    @Given("^a template named \"([^\"]*)\"$")
    public void a_template_named(String arg1) throws Throwable {
		Optional<InputStream> inputStreamOptional = getResourceAsStream(arg1);
        assert(inputStreamOptional.isPresent());
		inputStream = inputStreamOptional.get();
    }

    @When("^it is parsed$")
    public void it_is_parsed() throws Throwable {
        outputStream = new ByteArrayOutputStream();
        Parser.parse(inputStream, outputStream, new Database(), new Context(), new NamedScriptEngine());
    }

    @Then("^the results should be \"([^\"]*)\"$")
    public void the_results_should_be(String arg1) throws Throwable {
        assertEquals(arg1, outputStream.toString());
    }


}
