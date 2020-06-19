$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("1_creation_of_maintenance_request.feature");
formatter.feature({
  "line": 1,
  "name": "Creation of Maintenance Request",
  "description": "As a Site Engineer\r\nSo that I continue with the construction project\r\nI want to have working machinery",
  "id": "creation-of-maintenance-request",
  "keyword": "Feature"
});
formatter.before({
  "duration": 44069011,
  "status": "passed"
});
formatter.background({
  "line": 6,
  "name": "Maintenance Request",
  "description": "",
  "type": "background",
  "keyword": "Background"
});
formatter.step({
  "line": 7,
  "name": "the following inventory",
  "rows": [
    {
      "cells": [
        "id",
        "plantInfo",
        "serialNumber",
        "equipmentCondition"
      ],
      "line": 8
    },
    {
      "cells": [
        "1",
        "1",
        "exc-mn1.5-01",
        "SERVICEABLE"
      ],
      "line": 9
    }
  ],
  "keyword": "Given "
});
formatter.step({
  "line": 10,
  "name": "no maintenance request exists in the system",
  "keyword": "And "
});
formatter.match({
  "location": "MaintenanceAcceptanceTestSteps.the_following_inventory(DataTable)"
});
formatter.result({
  "duration": 539315831,
  "status": "passed"
});
formatter.match({
  "location": "MaintenanceAcceptanceTestSteps.no_maintenance_request_exists_in_the_system()"
});
formatter.result({
  "duration": 235869685,
  "status": "passed"
});
formatter.scenario({
  "line": 12,
  "name": "Create a maintenance request for a plant item successfully",
  "description": "",
  "id": "creation-of-maintenance-request;create-a-maintenance-request-for-a-plant-item-successfully",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 13,
  "name": "the site engineer with name \"Jacob\" creates a maintenance order with constructionSiteID \"2\", with description \"DESC2\" between \"2020-07-10\" and \"2020-07-20\" for the plant with id \"3\"",
  "keyword": "When "
});
formatter.step({
  "line": 14,
  "name": "a maintenance order in BuildIT is successfully created",
  "keyword": "Then "
});
formatter.step({
  "line": 15,
  "name": "the site engineer checks the state of the maintenance order after the response",
  "keyword": "When "
});
formatter.step({
  "line": 16,
  "name": "the state of the maintenance order should be \"ACCEPTED\"",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "Jacob",
      "offset": 29
    },
    {
      "val": "2",
      "offset": 89
    },
    {
      "val": "DESC2",
      "offset": 111
    },
    {
      "val": "2020-07-10",
      "offset": 127
    },
    {
      "val": "2020-07-20",
      "offset": 144
    },
    {
      "val": "3",
      "offset": 179
    }
  ],
  "location": "MaintenanceAcceptanceTestSteps.the_site_engineer_with_name_creates_a_maintenance_order_with_constructionSiteID_with_description_between_and_for_the_plant_with_id(String,String,String,String,String,String)"
});
formatter.result({
  "duration": 251744331,
  "status": "passed"
});
formatter.match({
  "location": "MaintenanceAcceptanceTestSteps.a_maintenance_order_in_BuildIT_is_successfully_created()"
});
formatter.result({
  "duration": 8523141,
  "status": "passed"
});
formatter.match({
  "location": "MaintenanceAcceptanceTestSteps.the_site_engineer_checks_the_state_of_the_maintenance_order_after_the_response()"
});
formatter.result({
  "duration": 10492226,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "ACCEPTED",
      "offset": 46
    }
  ],
  "location": "MaintenanceAcceptanceTestSteps.the_state_of_the_maintenance_order_should_be(String)"
});
formatter.result({
  "duration": 6130785,
  "status": "passed"
});
formatter.after({
  "duration": 74094646,
  "status": "passed"
});
formatter.uri("2_cancellation_of_maintenance_order.feature");
formatter.feature({
  "line": 1,
  "name": "Maintenance Order Cancellation",
  "description": "As a Site Engineer\r\nSo that I do not need to hire the plant item any longer\r\nI want to cancel the maintenance order",
  "id": "maintenance-order-cancellation",
  "keyword": "Feature"
});
formatter.before({
  "duration": 8681346,
  "status": "passed"
});
formatter.background({
  "line": 6,
  "name": "Maintenance Order",
  "description": "",
  "type": "background",
  "keyword": "Background"
});
formatter.step({
  "line": 7,
  "name": "the following maintenance orders in RentIT",
  "rows": [
    {
      "cells": [
        "id",
        "description",
        "siteEngineerName",
        "constructionSiteId",
        "startDate",
        "endDate",
        "plantId"
      ],
      "line": 8
    },
    {
      "cells": [
        "1",
        "DESC2",
        "Jacob",
        "10",
        "2020-07-10",
        "2020-07-20",
        "1"
      ],
      "line": 9
    }
  ],
  "keyword": "Given "
});
formatter.match({
  "location": "MaintenanceAcceptanceTestSteps.the_following_maintenance_orders_in_RentIT(DataTable)"
});
formatter.result({
  "duration": 7242007,
  "status": "passed"
});
formatter.scenario({
  "line": 12,
  "name": "Cancel a maintenance order",
  "description": "",
  "id": "maintenance-order-cancellation;cancel-a-maintenance-order",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 13,
  "name": "the site engineer \"Jacob\" queries the maintenance order with id \"2\" from RentIT",
  "keyword": "When "
});
formatter.step({
  "line": 14,
  "name": "the clerk updates the status of the maintenance order with \"ACCEPTED\"",
  "keyword": "Then "
});
formatter.step({
  "line": 15,
  "name": "the site site engineer requests cancellation of that maintenance order from BuildIT",
  "keyword": "And "
});
formatter.step({
  "line": 16,
  "name": "the site engineer queries the received response of the maintenance order",
  "keyword": "When "
});
formatter.match({
  "arguments": [
    {
      "val": "Jacob",
      "offset": 19
    },
    {
      "val": "2",
      "offset": 65
    }
  ],
  "location": "MaintenanceAcceptanceTestSteps.the_site_engineer_queries_the_maintenance_order_with_id_from_RentIT(String,String)"
});
formatter.result({
  "duration": 5832874,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "ACCEPTED",
      "offset": 60
    }
  ],
  "location": "MaintenanceAcceptanceTestSteps.the_clerk_updates_the_status_of_the_maintenance_order_with(String)"
});
formatter.result({
  "duration": 223630012,
  "status": "passed"
});
formatter.match({
  "location": "MaintenanceAcceptanceTestSteps.the_site_site_engineer_requests_cancellation_of_that_maintenance_order_from_BuildIT()"
});
formatter.result({
  "duration": 95852762,
  "status": "passed"
});
formatter.match({
  "location": "MaintenanceAcceptanceTestSteps.the_site_engineer_queries_the_received_response_of_the_maintenance_order()"
});
formatter.result({
  "duration": 4889299,
  "status": "passed"
});
formatter.after({
  "duration": 6650715,
  "status": "passed"
});
formatter.before({
  "duration": 6045075,
  "status": "passed"
});
formatter.background({
  "line": 6,
  "name": "Maintenance Order",
  "description": "",
  "type": "background",
  "keyword": "Background"
});
formatter.step({
  "line": 7,
  "name": "the following maintenance orders in RentIT",
  "rows": [
    {
      "cells": [
        "id",
        "description",
        "siteEngineerName",
        "constructionSiteId",
        "startDate",
        "endDate",
        "plantId"
      ],
      "line": 8
    },
    {
      "cells": [
        "1",
        "DESC2",
        "Jacob",
        "10",
        "2020-07-10",
        "2020-07-20",
        "1"
      ],
      "line": 9
    }
  ],
  "keyword": "Given "
});
formatter.match({
  "location": "MaintenanceAcceptanceTestSteps.the_following_maintenance_orders_in_RentIT(DataTable)"
});
formatter.result({
  "duration": 3619871,
  "status": "passed"
});
formatter.scenario({
  "line": 19,
  "name": "Cancellation of a completed maintenance order with error",
  "description": "",
  "id": "maintenance-order-cancellation;cancellation-of-a-completed-maintenance-order-with-error",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 20,
  "name": "the clerk approves and completes the maintenance order with id \"2\"",
  "keyword": "When "
});
formatter.step({
  "line": 21,
  "name": "the status of the maintenance order is \"COMPLETED\"",
  "keyword": "Then "
});
formatter.step({
  "line": 22,
  "name": "the site engineer \"Jacob\" cancels the completed maintenance order with id \"2\" from RentIT",
  "keyword": "When "
});
formatter.step({
  "line": 23,
  "name": "validation error messages are received",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "2",
      "offset": 64
    }
  ],
  "location": "MaintenanceAcceptanceTestSteps.the_clerk_approves_and_completes_the_maintenance_order_with_id(String)"
});
formatter.result({
  "duration": 17751602,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "COMPLETED",
      "offset": 40
    }
  ],
  "location": "MaintenanceAcceptanceTestSteps.the_status_of_the_maintenance_order_is(String)"
});
formatter.result({
  "duration": 11153370,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Jacob",
      "offset": 19
    },
    {
      "val": "2",
      "offset": 75
    }
  ],
  "location": "MaintenanceAcceptanceTestSteps.the_site_engineer_cancels_the_completed_maintenance_order_with_id_from_RentIT(String,String)"
});
formatter.result({
  "duration": 30189882,
  "status": "passed"
});
formatter.match({
  "location": "MaintenanceAcceptanceTestSteps.validation_error_messages_are_received()"
});
formatter.result({
  "duration": 70607,
  "status": "passed"
});
formatter.after({
  "duration": 7599577,
  "status": "passed"
});
});