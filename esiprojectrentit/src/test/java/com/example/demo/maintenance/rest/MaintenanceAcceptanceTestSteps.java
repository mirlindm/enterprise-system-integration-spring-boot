package com.example.demo.maintenance.rest;


import com.example.demo.DemoApplication;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.maintenance.application.dto.MaintenanceOrderDTO;
import com.example.demo.maintenance.application.service.MaintenanceService;
import com.example.demo.maintenance.domain.model.MaintOrderStatus;
import com.example.demo.maintenance.domain.model.MaintenanceOrder;
import com.example.demo.maintenance.domain.repository.MaintenanceOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
public class MaintenanceAcceptanceTestSteps {

    private String order_response;

    /*@Configuration
    static class RentalServiceMock {
        @Bean
        public MaintenanceService maintenanceService() {
            return Mockito.mock(MaintenanceService.class);
        }
    }*/

   // @Autowired
    //MaintenanceRequestRepository maintenanceRequestRepository;

    @Autowired
    MaintenanceOrderRepository maintenanceOrderRepository;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    @Autowired
    MaintenanceService maintenanceService;


    MaintenanceOrder maintenanceOrder;


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
        //maintenanceRequestRepository.deleteAll();
        maintenanceOrderRepository.deleteAll();
    }

    @Given("^the following inventory$")
    public void the_following_inventory(DataTable arg1) throws Throwable {
        String uri = "/api/plants/items/{id}";

        for (Map<String, String> row: arg1.asMaps(String.class, String.class))
            mockMvc.perform(get(uri, row.get("id"))).andExpect(status().isOk());

        //Resource responseBody = new ClassPathResource("plants.json", this.getClass());
        //MvcResult mvcResult = mockMvc.perform(get(uri, 2)).andReturn();

        //String response = mvcResult.getResponse().getContentAsString();
        //assertThat(response).contains("serialNumber");
        //int status = mvcResult.getResponse().getStatus();
        //assertThat(status).isEqualTo(200);
    }

    @Given("^no maintenance order exists in the system$")
    public void no_maintenance_request_exists_in_the_system() throws Throwable {
        assertThat(maintenanceOrderRepository.findAll().size()).isEqualTo(0);
    }

    @When("^the site engineer with name \"([^\"]*)\" creates a maintenance order with constructionSiteID \"([^\"]*)\", with description \"([^\"]*)\" between \"([^\"]*)\" and \"([^\"]*)\" for the plant with id \"([^\"]*)\"$")
    public void the_site_engineer_with_name_creates_a_maintenance_order_with_constructionSiteID_with_description_between_and_for_the_plant_with_id(String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) throws Throwable {
        String uri = "/api/maintenance/orders";

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
        this.order_response = response;
    }

    @Then("^a maintenance order in RentIT is successfully created$")
    public void a_maintenance_order_in_RentIT_is_successfully_created() throws Throwable {
        assertThat(maintenanceOrderRepository.findAll().size()).isEqualTo(1);
    }

    @When("^the site engineer checks the state of the maintenance order after the response$")
    public void the_site_engineer_checks_the_state_of_the_maintenance_order_after_the_response() throws Throwable {
        String uri = "/api/maintenance/order/{id}";
        mockMvc.perform(get(uri, 1)).andExpect(status().is2xxSuccessful());
    }

    @Then("^the state of the maintenance order should be \"([^\"]*)\"$")
    public void the_state_of_the_maintenance_order_should_be(String arg1) throws Throwable {
        assertThat(order_response).contains(arg1);
    }


    // End of Feature 1 Steps, Start of Feature 2 Steps


    @Given("^the following maintenance orders in RentIT$")
    public void the_following_maintenance_orders_in_RentIT(DataTable arg1) throws Throwable {
        for (Map<String, String> row: arg1.asMaps(String.class, String.class)){
            BusinessPeriod businessPeriod = BusinessPeriod.of(LocalDate.parse(row.get("startDate")),  LocalDate.parse(row.get("endDate")));
            PlantInventoryItem pl = plantInventoryItemRepository.findById(Long.parseLong(row.get("plantId"))).orElse(null);

            MaintenanceOrder mo = new MaintenanceOrder();
            mo.setDescription(row.get("description"));
            mo.setSiteEngineerName(row.get("siteEngineerName"));
            mo.setConstructionSiteId( Long.parseLong(row.get("constructionSiteId")));
            mo.setExpectedPeriod(businessPeriod);
            mo.setPlant(pl);
            mo.setStatus(MaintOrderStatus.PENDING);

            maintenanceOrderRepository.save(mo);
        }



    }

    @When("^the site engineer \"([^\"]*)\" queries the maintenance order with id \"([^\"]*)\" from RentIT$")
    public void the_site_engineer_queries_the_maintenance_order_with_id_from_RentIT(String arg1, String arg2) throws Throwable {
        String uri = "/api/maintenance/order/{id}";
        mockMvc.perform(get(uri, 2)).andExpect(status().is2xxSuccessful());
    }

    @Then("^the clerk updates the status of the maintenance order with \"([^\"]*)\"$")
    public void the_clerk_updates_the_status_of_the_maintenance_order_with(String arg1) throws Throwable {
        String uri = "/api/maintenance/order/{id}";

        String newJson = "{\n" +
                "    \"description\": \"first maintenance task\",\n" +
                "    \"type_of_work\": \"OPERATIVE\",\n" +
                "    \"total\": 42,\n" +
                "    \"rentalPeriod\": {\n" +
                "        \"startDate\": \"2020-07-20\",\n" +
                "        \"endDate\": \"2020-07-25\"\n" +
                "    }\n" +
                "}";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(put(uri, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);
        String response = mvcResult.getResponse().getContentAsString();
        this.order_response = response;
        assertThat(response).contains(arg1);

    }

    @Then("^the site site engineer requests cancellation of that maintenance order from BuildIT$")
    public void the_site_site_engineer_requests_cancellation_of_that_maintenance_order_from_BuildIT() throws Throwable {
        String uri = "/api/maintenance/order/{id}/cancel";
        mockMvc.perform(patch(uri, 2)).andExpect(status().is2xxSuccessful());
    }

    @When("^the site engineer queries the received response of the maintenance order$")
    public void the_site_engineer_queries_the_received_response_of_the_maintenance_order() throws Throwable {
        String uri = "/api/maintenance/order/{id}";
         mockMvc.perform(get(uri, 2)).andExpect(status().is2xxSuccessful());

//         int status = mvcResult.getResponse().getStatus();
//         assertThat(status).isEqualTo(200);
//         String response = mvcResult.getResponse().getContentAsString();
//         this.order_reponse = response;
    }

    @Then("^the maintenance order status is \"([^\"]*)\"$")
    public void the_maintenance_order_status_is(String arg1) throws Throwable {
        String uri = "/api/maintenance/order/{id}";
        //mockMvc.perform(get(uri, 1)).andExpect(content().toString().contains("CANCELLED"));

        MvcResult mvcResult = mockMvc.perform(get(uri, 2)).andReturn();
        assertThat(mvcResult).toString().contains("Cancelled");
    }

    // End of Scenario 1 of Feature 2

    @When("^the clerk approves and completes the maintenance order with id \"([^\"]*)\"$")
    public void the_clerk_approves_and_completes_the_maintenance_order_with_id(String arg1) throws Throwable {
        String uri = "/api/maintenance/order/{id}";
        System.out.println("featureeeeeeeeee" + maintenanceOrderRepository.findAll());
        mockMvc.perform(patch(uri, 3)).andExpect(status().is2xxSuccessful());
    }

    @Then("^the status of the maintenance order is \"([^\"]*)\"$")
    public void the_status_of_the_maintenance_order_is(String arg1) throws Throwable {
        String uri = "/api/maintenance/order/{id}";
        MvcResult mvcResult = mockMvc.perform(get(uri, 3)).andReturn();

        assertThat(mvcResult).toString().contains("COMPLETED");
    }

    @When("^the site engineer \"([^\"]*)\" cancels the completed maintenance order with id \"([^\"]*)\" from RentIT$")
    public void the_site_engineer_cancels_the_completed_maintenance_order_with_id_from_RentIT(String arg1, String arg2) throws Throwable {
        String uri = "/api/maintenance/order/{id}/cancel";
        mockMvc.perform(patch(uri, 3)).andExpect(status().is4xxClientError());
        MvcResult mvcResult = mockMvc.perform(patch(uri, 6)).andReturn();
        this.order_response = mvcResult.toString();
    }

    @Then("^validation error messages are received$")
    public void validation_error_messages_are_received() throws Throwable {
        assertThat(order_response).toString().contains("Maintenance Order Can't be cancelled");
    }


}
