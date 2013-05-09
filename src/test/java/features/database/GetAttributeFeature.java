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
public class GetAttributeFeature {
    private Database database;
    private String value;

    @Given("^a Database loaded from \"([^\"]*)\"$")
    public void a_Database_loaded_from(List<String> filenames) throws Throwable {
        database = Database.parse(filenames.toArray(new String[0]));
        assertNotNull(database);
    }

    @When("^the value of \"([^\"]*)\" is retreived$")
    public void the_value_of_is_retreived(String arg1) throws Throwable {
        value = database.attr(arg1);
        assertNotNull(arg1);
    }

    @Then("^it should equal \"([^\"]*)\"$")
    public void it_should_equal(String arg1) throws Throwable {
      assertEquals(arg1,value);
    }

}
