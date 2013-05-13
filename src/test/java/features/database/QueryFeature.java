package features.database;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import scotty.database.Context;
import scotty.database.Database;
import scotty.database.parser.NamedContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 *
 */
public class QueryFeature {
    private Database database;
    private Context context;
    private String value;
    private List<Context> results;

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

    @Given("^the context based on \"([^\"]*)\"$")
    public void the_context_based_on(String arg1) throws Throwable {
        context = new Context(null, arg1);
        assertNotNull(context);
    }

    @Then("^query should yield (\\d+) instances$")
    public void query_should_yield_instances(int arg1) throws Throwable {
        results = database.query(context);
        assertEquals(arg1, results.size());
    }

    @Then("^the first instance should be \"([^\"]*)\"$")
    public void the_first_instance_should_be(String arg1) throws Throwable {
        NamedContext namedContext = (NamedContext) results.get(0);
        assertEquals(arg1, namedContext.getName());
    }

}
