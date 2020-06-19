Feature: Maintenance Order Cancellation
  As a Site Engineer
  So that I do not need to hire the plant item any longer
  I want to cancel the maintenance order

  Background: Maintenance Order
    Given the following maintenance orders in RentIT
      | id | description | siteEngineerName | constructionSiteId | startDate  | endDate    | plantId |
      |  1 | DESC2       | Jacob            | 10                 | 2020-07-10 | 2020-07-20 | 1       |


  Scenario: Cancel a maintenance order
    When the site engineer "Jacob" queries the maintenance order with id "2" from RentIT
    Then the clerk updates the status of the maintenance order with "ACCEPTED"
    And the site site engineer requests cancellation of that maintenance order from BuildIT
    When the site engineer queries the received response of the maintenance order


  Scenario: Cancellation of a completed maintenance order with error
    When the clerk approves and completes the maintenance order with id "2"
    Then the status of the maintenance order is "COMPLETED"
    When the site engineer "Jacob" cancels the completed maintenance order with id "2" from RentIT
    Then validation error messages are received