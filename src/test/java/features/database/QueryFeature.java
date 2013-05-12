package features.database;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import scotty.database.Database;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static junit.framework.TestCase.assertTrue;

/**
 *
 */
public class QueryFeature {
	private Database database;

	@Given("^a Database based on$")
	public void a_Database_based_on(String arg1) throws Throwable {
		try (InputStream inputStream = new ByteArrayInputStream(arg1.getBytes())) {
			database = Database.parse(inputStream);
		}
		assertTrue(database.getChildren().size() > 0);
	}

	@Then("^success$")
	public void success() throws Throwable {

	}


}
