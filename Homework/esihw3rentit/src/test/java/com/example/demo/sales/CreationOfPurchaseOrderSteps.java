package com.example.demo.sales;

import com.example.demo.DemoApplication;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDateInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
public class CreationOfPurchaseOrderSteps {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;
    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    private List<PlantInventoryEntryDTO> availablePlants;

    @Before  // Use `Before` from Cucumber library
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @After  // Use `After` from Cucumber library
    public void tearOff() {
        purchaseOrderRepository.deleteAll();
        plantInventoryItemRepository.deleteAll();
        plantInventoryEntryRepository.deleteAll();
    }

    @Given("^the following plant catalog$")
    public void the_following_plant_catalog(List<PlantInventoryEntry> entries) throws Throwable {
        plantInventoryEntryRepository.saveAll(entries);
    }

    @Given("^the following inventory$")
    public void the_following_inventory(DataTable table) throws Throwable {
        for (Map<String, String> row: table.asMaps(String.class, String.class))
            plantInventoryItemRepository.save(
                    PlantInventoryItem.of(
                            Long.parseLong(row.get("id")),
                            row.get("serialNumber"),
                            EquipmentCondition.valueOf(row.get("equipmentCondition")),
                            plantInventoryEntryRepository.findById(Long.parseLong(row.get("plantInfo"))).orElse(null)
                    )
            );
    }

    @Given("^no purchase order exists in the system$")
    public void no_purchase_order_exists_in_the_system() throws Throwable {
        assertThat(purchaseOrderRepository.findAll().size()).isZero();
    }

    @When("^the customer queries the plant catalog for an \"([^\"]*)\" available from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void the_customer_queries_the_plant_catalog_for_an_available_from_to(String plantName, String startDate, String endDate) throws Throwable {

        String url = String.format("/api/sales/plants?name=%s&startDate=%s&endDate=%s", plantName, startDate, endDate);

        MvcResult result = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode plantsJSON = mapper.readTree(result.getResponse().getContentAsString())
                .path("_embedded")
                .path("plantInventoryEntryDToes");

        availablePlants = mapper.readValue(plantsJSON.toString(), new TypeReference<List<PlantInventoryEntryDTO>>() { });

    }

    @Then("^(\\d+) plants are shown$")
    public void plants_are_shown(int arg1) throws Throwable {
        assertThat(availablePlants.size()).isEqualTo(arg1);
    }

    @When("^the customer selects a \"([^\"]*)\"$")
    public void the_customer_selects_a(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^a purchase order should be created with a total price of (\\d+)\\.(\\d+)$")
    public void a_purchase_order_should_be_created_with_a_total_price_of(int arg1, int arg2) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

}
