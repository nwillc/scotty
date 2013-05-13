Feature: The Database should provide a way to query instances based on a context.

  Background:
    Given a Database based on
    """
    <type name="host">
    <attribute name="company" value="acme"/>
    <attribute name="address" value="127.0.0.1"/>
	<context>
        <attribute name="env" value="dev"/>
		<instance name="devbox1">
			<attribute name="address" value="192.0.0.1"/>
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



