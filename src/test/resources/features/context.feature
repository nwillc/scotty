Feature: Context classes, holding attributes, should form a tree and provide attribute retrieval, up the tree.

  Scenario Outline: A Context should be able to be instantiated from an assignment list
    Given a Context instantiated with "<assignments>"
    When you search for "<label>"
    Then it value should be "<value>"

  Examples:
    | assignments       | label | value |
    | foo=bar           | foo   | bar   |
    | foo=bar , one=two | one   | two   |
    | one = two         | one   | two   |

  Scenario Outline: Requesting a value from a child context should first search the child and then check the parent.
    Given a Context instantiated with "<assignments>"
    And a its child instantiated with "<childAssignments>"
    When you search for "<label>"
    Then it value should be "<value>"

  Examples:
    | assignments | childAssignments | label | value |
    | one=two     | foo=bar          | one   | two   |
    | one=two     | foo=bar          | foo   | bar   |
    | one=two     | one=bar          | one   | bar   |

  Scenario Outline: Requesting a Contexts keySet should yield the keys of the Context and its parents.
    Given a Context instantiated with "<assignments>"
    And a its child instantiated with "<childAssignments1>"
    Then the keySet should contain "<keys1>"
    And a its child instantiated with "<childAssignments2>"
    Then the keySet should contain "<keys2>"

  Examples:
    | assignments     | childAssignments1 | keys1       | childAssignments2 | keys2         |
    | one=two         | foo=bar           | one,foo     | a=b               | one,foo,a     |
    | one=two,foo=bar | one=fff,two=ggg   | one,foo,two | a=b               | one,foo,two,a |
