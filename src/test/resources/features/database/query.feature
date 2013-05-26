Feature: The Database should provide a way to query instances based on a context.

  Background:
    Given a Database based on
    """
    <type name="host">
    <attribute name="foo"/>
    <attribute name="company" value="acme"/>
    <attribute name="address" value="127.0.0.1"/>
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
    And you find "<attribute>"
    Then it should return "<string>"

  Examples:
    | attribute            | string    |
    | host.devbox1.address | 192.0.0.1 |
    | host.company         | acme      |
    | host.prod1.address   | 127.0.0.1 |
    | host.prod1.env       | prod      |


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


