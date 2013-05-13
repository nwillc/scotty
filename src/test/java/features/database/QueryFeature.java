package features.database;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import scotty.database.Database;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static junit.framework.TestCase.*;

/**
 *
 */
public class QueryFeature {
	private Database database;
	private String value;

	@Given("^a Database based on$")
	public void a_Database_based_on(String arg1) throws Throwable {
		try (InputStream inputStream = new ByteArrayInputStream(arg1.getBytes())) {
			database = Database.parse(inputStream);
		}
		assertTrue(database.getChildren().size() > 0);
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


}
