Feature: The Database should provide a way to get the value of fully qualified attribute names.

  Scenario Outline: retrieve a value by attribute name
    Given a Database loaded from "<filenames>"
    When the value of "<attribute>" is retreived
    Then it should equal "<value>"

  Examples:
    | filenames                                                              | attribute                | value                   |
    | ./target/test-classes/host.xml                                        | host.prod1.env           | prod                    |
    | ./target/test-classes/host.xml                                        | host.prod1.company       | acme                    |
    | ./target/test-classes/host.xml,./target/test-classes/environment.xml | environment.dev.longname | Development Environment |
