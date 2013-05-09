package features.template;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static junit.framework.TestCase.assertEquals;

/**
 *
 */
public class CharQueueFeature {
    private scotty.template.CharQueue charQueue1;
    private scotty.template.CharQueue charQueue2;
    private OutputStream outputStream;

    @Given("^an instance of CharQueue with capacity (\\d+)$")
    public void an_instance_of_CharQueue_with_capacity(int arg1) throws Throwable {
        outputStream = new ByteArrayOutputStream();
        charQueue1 = new scotty.template.CharQueue(arg1, outputStream);
    }

    @When("^add characters contained in string \"([^\"]*)\"$")
    public void add_characters_contained_in_string(String arg1) throws Throwable {
        charQueue1.add(arg1);
    }

    @Then("^CharQueue should have value of \"([^\"]*)\"$")
    public void CharQueue_should_have_value_of(String arg1) throws Throwable {
        assertEquals(arg1, charQueue1.toString());
    }

    @Then("^excess should be \"([^\"]*)\"$")
    public void excess_should_be(String arg1) throws Throwable {
        assertEquals(arg1, outputStream.toString());
    }

    @Then("^it's size should be (\\d+)$")
    public void it_s_size_should_be(int arg1) throws Throwable {
        assertEquals(arg1, charQueue1.size());
    }


    @When("^another instance of CharQueue containing \"([^\"]*)\"$")
    public void another_instance_of_CharQueue_containing(String arg1) throws Throwable {
        charQueue2 = new scotty.template.CharQueue(arg1, null);
    }

    @Then("^compareTo should return (-?\\d+)$")
    public void compareTo_should_return(int arg1) throws Throwable {
        assertEquals(arg1, charQueue1.compareTo(charQueue2));
    }


}
