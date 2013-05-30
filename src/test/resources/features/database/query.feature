Feature: The Database should provide a way to query instances based on a context.

  Background:
    Given a Database based on
    """
    <type name="host">
    <attribute name="foo"/>
    <attribute name="company" value="acme"/>
    <attribute name="address" value="127.0.0.1"/>
    <attribute name="flag1"/>
	<context>
	    <attribute name="flag1" value="true"/>
        <attribute name="env" value="dev"/>
		<instance name="devbox1">
			<attribute name="address" value="192.0.0.1"/>
			<instance name="devbox1-qa">
			    <attribute name="env" value="qa"/>
			    <attribute name="flag2" value="true"/>
			</instance>
		</instance>
	</context>
	<context>
		<attribute name="env" value="prod"/>
		<instance name="prod1"/>
	</context>
    </type>
    """

  Scenario Outline: Query and instance by a context of attributes
    Given the database
    And you find "<instance>" "<attribute>"
    Then it should return "<string>"

  Examples:
    | instance         | attribute | string    |
    | instance=devbox1 | address   | 192.0.0.1 |
    | type=host        | company   | acme      |
    | instance=prod1   | address   | 127.0.0.1 |
    | instance=prod1   | env       | prod      |


  Scenario Outline: Retreive a lists of instances using a query with a context
    Given the database
    And the context based on "<assignments>"
    Then query should yield <count> instances
    And the first instance should be "<name>"

  Examples: Successful
    | assignments | count | name       |
    | env=dev     | 1     | devbox1    |
    | flag2=true  | 1     | devbox1-qa |
    | flag1=true  | 2     | devbox1    |

  Examples: Failing
    | assignments     | count | name |
    | foo=bar         | 0     |      |
    | env=dev,foo=bar | 0     |      |

  Scenario Outline: Retrieve a set of attribute values using a query with a context
    Given the database
    And the context based on "<assignments>"
    And and the attribute "<attribute>"
    Then it should yield set "<set>"

  Examples:
    | assignments          | attribute | set         |
    | type=host            | env       | dev,prod,qa |
    | type=host,flag1=true | env       | dev,qa      |
