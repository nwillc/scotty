Feature: The Database should provide a way to query instances based on a context.

  Scenario: Query and instance by a context of attributes
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
		<instance name="devbox2">
			<attribute name="address" value="192.0.0.2"/>
		</instance>
	</context>
	<context>
		<attribute name="env" value="prod"/>
		   <instance name="prod1">
			<attribute name="address" value="192.0.0.3"/>
		</instance>
	</context>
    </type>
    """
    Then success

