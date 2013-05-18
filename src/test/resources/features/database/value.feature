Feature: A value is a collection of strings that should match to any one of the collection.

  Scenario Outline: Matching a string to a value should match any of the values strings.
    Given a value of "<value>"
    And a "<string>"
    When they are matched
    Then if should return "<boolean>"

  Examples: Positive matches
    | value   | string | boolean |
    | foo     | foo    | true    |
    | foo bar | foo    | true    |
    | foo bar | bar    | true    |

  Examples: Failing matches
    | value   | string | boolean |
    | foo,bar | foo    | false   |
    | foo bar | baz    | false   |

  Scenario Outline: The string representation of a value should be a comma separated list of its members
    Given a value of "<value>"
    Then its string representation should be "<string>"

  Examples:
    | value   | string  |
    | foo bar | foo,bar |
