/*
 * (c) Copyright Selerity, Inc. 2009-2013. All rights reserved. This source code is confidential
 * and proprietary information of Selerity Inc. and may be used only by a recipient designated
 * by and for the purposes permitted by Selerity Inc. in writing.  Reproduction of, dissemination
 * of, modifications to or creation of derivative works from this source code, whether in source
 * or binary forms, by any means and in any form or manner, is expressly prohibited, except with
 * the prior written permission of Selerity Inc..  THIS CODE AND INFORMATION ARE PROVIDED "AS IS"
 * WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE. This notice may not be
 * removed from the software by any user thereof.
 */

package features.template;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import scotty.database.Context;
import scotty.database.Database;
import scotty.database.parser.Utilities;
import scotty.template.Parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 *
 */
public class ImportFeature {
	private InputStream inputStream;
	private OutputStream outputStream;

	@Given("^a template named \"([^\"]*)\"$")
	public void a_template_named(String arg1) throws Throwable {
		inputStream = Utilities.getResourceAsStream(arg1);
		assertNotNull(inputStream);
	}

	@When("^it is parsed$")
	public void it_is_parsed() throws Throwable {
		outputStream = new ByteArrayOutputStream();
		Parser.parse(inputStream, outputStream, new Database(), new Context());
	}

	@Then("^the results should be \"([^\"]*)\"$")
	public void the_results_should_be(String arg1) throws Throwable {
		assertEquals(arg1, outputStream.toString());
	}


}
