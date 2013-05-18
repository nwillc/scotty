Feature: Parsing an SCoTTY xml database.

  Scenario Outline: Creating an SCoTTY database from an XML file.
    Given the XML "<filelist>"
    When parsed by the Database class
    Then you should receive a Database with <count> Types
    And it should contain types "<typelist>"

  Examples:
    | filelist                                                             | count | typelist         |
    | ./target/test-classes/host.xml                                       | 1     | host             |
    | ./target/test-classes/environment.xml                                | 1     | environment      |
    | ./target/test-classes/host.xml,./target/test-classes/environment.xml | 2     | host,environment |


  Scenario Outline: Attribute values can be multi value.
    Given the XML "./target/test-classes/multivalue.xml"
    When parsed by the Database class
    Then the value of "<attr>" should be "<value>"

  Examples:
    | attr                   | value         |
    | multivalue.one.array   | one           |
    | multivalue.three.array | one,two,three |
