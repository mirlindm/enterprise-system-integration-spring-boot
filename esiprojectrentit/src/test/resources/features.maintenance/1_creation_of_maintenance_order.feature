Feature: Creation of Maintenance Order
  As a Site Engineer
  So that I continue with the construction project
  I want to create a maintenance request so that an order is created in RentIT

  Background: Maintenance Order
    Given the following inventory
      | id | plantInfo | serialNumber | equipmentCondition |
      |  1 |     1     | exc-mn1.5-01 | SERVICEABLE        |
    And no maintenance order exists in the system

  Scenario: Create a maintenance order for a plant item successfully
    When the site engineer with name "Jacob" creates a maintenance order with constructionSiteID "2", with description "DESC2" between "2020-07-10" and "2020-07-20" for the plant with id "3"
    Then a maintenance order in RentIT is successfully created
    When the site engineer checks the state of the maintenance order after the response
    Then the state of the maintenance order should be "PENDING"
