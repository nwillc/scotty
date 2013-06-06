Feature: A scotty template should be able to import another scotty template.

  Scenario: The output of parsing a template that imports another should be a contiguous combination of the two.
    Given a template named "outer.scotty"
    When it is parsed
    Then the results should be "one,two,three"

