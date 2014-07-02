Feature: Parsing an SCoTTY xml database.

  Scenario Outline: Creating an SCoTTY database from an XML file.
    Given the XML "<filelist>"
    When parsed by the Database class
    Then you should receive a Database with <count> Types
    And it should contain types "<typelist>"

  Examples:
    | filelist                                                               | count | typelist         |
    | ./build/resources/test/host.xml                                        | 1     | host             |
    | ./build/resources/test/environment.xml                                 | 1     | environment      |
    | ./build/resources/test/host.xml,./build/resources/test/environment.xml | 2     | host,environment |


  Scenario Outline: Attribute values can be multi value.
    Given the XML "./build/resources/test/multivalue.xml"
    When parsed by the Database class
    Then the value of "<instance>" "<attr>" should be "<value>"

  Examples:
    | instance       | attr  | value         |
    | instance=one   | array | one           |
    | instance=three | array | one,two,three |
