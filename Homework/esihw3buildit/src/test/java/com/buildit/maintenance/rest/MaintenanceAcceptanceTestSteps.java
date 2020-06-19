package com.buildit.maintenance.rest;

import com.buildit.BuilditApplication;
import com.buildit.maintenance.application.service.MaintenanceService;
import com.buildit.maintenance.domain.model.PlantInventoryItem;
import com.buildit.maintenance.domain.repositories.MaintenanceOrderRepository;
import com.buildit.maintenance.domain.repositories.MaintenanceRequestRepository;
import com.buildit.maintenance.domain.repositories.PlantInventoryItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
//import cucumber.runtime.io.Resource;
import org.springframework.core.io.Resource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = BuilditApplication.class)
@WebAppConfiguration
public class MaintenanceAcceptanceTestSteps {
    private String order_reponse;

    /*@Configuration
    static class RentalServiceMock {
        @Bean
        public MaintenanceService maintenanceService() {
            return Mockito.mock(MaintenanceService.class);
        }
    }*/

    @Autowired
    MaintenanceRequestRepository maintenanceRequestRepository;

    @Autowired
    MaintenanceOrderRepository maintenanceOrderRepository;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    @Autowired
    MaintenanceService maintenanceService;


    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Before  // Use `Before` from Cucumber library
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @After  // Use `After` from Cucumber library
    public void tearOff() {
        maintenanceRequestRepository.deleteAll();
        maintenanceOrderRepository.deleteAll();
        plantInventoryItemRepository.deleteAll();
    }


    @Given("^the following inventory$")
    public void the_following_inventory(DataTable arg1) throws Throwable {
        String uri = "/api/maintenance/plants/items/{id}";

        for (Map<String, String> row: arg1.asMaps(String.class, String.class))
            mockMvc.perform(get(uri, row.get("id"))).andExpect(status().isOk());

        //Resource responseBody = new ClassPathResource("plants.json", this.getClass());
        //MvcResult mvcResult = mockMvc.perform(get(uri, 2)).andReturn();

        //String response = mvcResult.getResponse().getContentAsString();
        //assertThat(response).contains("serialNumber");
        //int status = mvcResult.getResponse().getStatus();
        //assertThat(status).isEqualTo(200);
    }

    @Given("^no maintenance request exists in the system$")
    public void no_maintenance_request_exists_in_the_system() throws Throwable {
        assertThat(maintenanceRequestRepository.findAll().size()).isEqualTo(0);
    }

    @When("^the site engineer with name \"([^\"]*)\" creates a maintenance request with constructionSiteID \"([^\"]*)\", with description \"([^\"]*)\" between \"([^\"]*)\" and \"([^\"]*)\" for the plant with id \"([^\"]*)\"$")
    public void the_site_engineer_with_name_creates_a_maintenance_request_with_constructionSiteID_with_description_between_and_for_the_plant_with_id(String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) throws Throwable {
        String uri = "/api/maintenance/requests";

        String newJson = "{\n" +
                "\t  \"expectedPeriod\": {\n" +
                "        \"startDate\": \"2020-07-10\",\n" +
                "        \"endDate\": \"2020-07-20\"\n" +
                "    },\n" +
                "    \"siteEngineerName\": \"Jacob\",\n" +
                "    \"description\": \"DESC2\",\n" +
                "    \"constructionSiteId\": 2,\n" +
                "    \"plantID\": 3\n" +
                "}";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(201);
        String response = mvcResult.getResponse().getContentAsString();
        this.order_reponse = response;
    }

    @Then("^a maintenance request in BuildIT is successfully created$")
    public void a_maintenance_request_in_BuildIT_is_successfully_created() throws Throwable {
        assertThat(maintenanceRequestRepository.findAll().size()).isEqualTo(1);
    }

    @Then("^the corresponding maintenance order is created in RentIT$")
    public void the_corresponding_maintenance_order_is_created_in_RentIT() throws Throwable {
        String uri = "/api/maintenance/orders/{id}";

        String href = maintenanceRequestRepository.findAll().get(0).getMo().get_xlink().getHref();
        String mo_id = href.substring(href.lastIndexOf('/') + 1);
        mockMvc.perform(get(uri, mo_id)).andExpect(status().is2xxSuccessful());
    }

    @Then("^the maintenance order response is received$")
    public void the_maintenance_order_response_is_received() throws Throwable {
        assertThat(order_reponse).contains("DESC2");
        assertThat(order_reponse).contains("Jacob");
    }

    @When("^the site engineer checks the state of the maintenance request after the response$")
    public void the_site_engineer_checks_the_state_of_the_maintenance_request_after_the_response() throws Throwable {
        String uri = "/api/maintenance/requests/{id}";
        mockMvc.perform(get(uri, 1)).andExpect(status().is2xxSuccessful());
    }

    @Then("^the state of the maintenance request should be \"([^\"]*)\"$")
    public void the_state_of_the_maintenance_request_should_be(String arg1) throws Throwable {
        assertThat(order_reponse).contains("ACCEPTED");
    }






}
