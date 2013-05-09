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
		assertEquals(arg1, database.size());
	}

	@Then("^it should contain types \"([^\\\"]*)\"$")
	public void it_should_contain_type_host(List<String> types) throws Throwable {
		for (String typeName : types) {
			assertTrue(database.containsKey(typeName));
		}
	}
}
