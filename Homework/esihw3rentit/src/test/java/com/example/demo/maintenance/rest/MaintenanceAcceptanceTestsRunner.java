package com.example.demo.maintenance.rest;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin={"pretty","html:target/cucumber"},
        features="src/test/resources/features.maintenance/",
        glue="com.example.demo.maintenance")
public class MaintenanceAcceptanceTestsRunner {
}
