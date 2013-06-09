Feature: A scotty template should be able to import another scotty template.

  Scenario Outline: The output of parsing a template that imports another should be a contiguous combination of the two.
    Given a template named "<template>"
    When it is parsed
    Then the results should be "<output>"

  Examples:
    | template      | output        |
    | outer1.scotty | one,two,three |
    | outer2.scotty | one,TWO,two   |