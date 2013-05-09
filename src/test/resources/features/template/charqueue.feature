Feature: Offer a fixed size queue of characters that, retains only its capacity and overflows into a provided output stream.

  Scenario Outline: Adding characters to a CharQueue so increase it's length until capacity
    Given an instance of CharQueue with capacity <capacity>
    When add characters contained in string "<string>"
    Then it's size should be <size>

  Examples:
    | capacity | string | size |
    | 10       | hello  | 5    |
    | 3        | hello  | 3    |
    | 10       |        | 0    |

  Scenario Outline: Add characters to a fixed size CharQueue and insure it retains the proper amount and the excess overflow into the stream.
    Given an instance of CharQueue with capacity <capacity>
    When add characters contained in string "<string>"
    Then CharQueue should have value of "<queued>"
    And excess should be "<excess>"

  Examples:
    | capacity | string | queued | excess |
    | 3        | abcd   | bcd    | a      |
    | 10       | abc    | abc    |        |
    | 3        | hello  | llo    | he     |


  Scenario Outline: CharQueues should be comparable.
    Given an instance of CharQueue with capacity <capacity>
    When add characters contained in string "<string1>"
    And another instance of CharQueue containing "<string2>"
    Then compareTo should return <value>

  Examples:
    | capacity | string1 | string2 | value |
    | 3        | abcd    | bcd     | 0     |
    | 5        | abc     | abc     | 0     |
    | 3        | bbb     | aaa     | 1     |
    | 4        | bbb     | ccc     | -1    |
    | 2        | bb      | bbb     | -1    |
    | 3        | bbb     | bb      | 1     |

