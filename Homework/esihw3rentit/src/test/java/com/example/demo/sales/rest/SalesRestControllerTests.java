package com.example.demo.sales.rest;

import com.example.demo.DemoApplication;
import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class) // Check if the name of this class is correct or not
@WebAppConfiguration
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SalesRestControllerTests {
    @Autowired
    PlantInventoryEntryRepository repo;

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @Sql("/plants-dataset.sql")
    public void testGetAllPlants() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/sales/plants?name=exc&startDate=2020-08-14&endDate=2020-08-25")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode plantsJSON = mapper.readTree(result.getResponse().getContentAsString())
                .path("_embedded")
                .path("plantInventoryEntryDToes");
        List<PlantInventoryEntryDTO> plants  = mapper.readValue(plantsJSON.toString(), new TypeReference<List<PlantInventoryEntryDTO>>() { });

        assertThat(plants.size()).isEqualTo(3);

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plants.get(1));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.of(2020, 8, 14), LocalDate.of(2020, 8, 25)));

        mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @Test
    @Sql("/plants-dataset.sql")
    public void testPurchaseOrderAcceptance() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/sales/plants?name=exc&startDate=2020-08-14&endDate=2020-08-25")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode plantsJSON = mapper.readTree(result.getResponse().getContentAsString())
                .path("_embedded")
                .path("plantInventoryEntryDToes");
        List<PlantInventoryEntryDTO> plants  = mapper.readValue(plantsJSON.toString(), new TypeReference<List<PlantInventoryEntryDTO>>() { });


        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plants.get(2));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.of(2020, 8, 14), LocalDate.of(2020, 8, 25)));

        result = mockMvc.perform(post("/api/sales/orders")
                .content(mapper.writeValueAsString(order))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", not(isEmptyOrNullString())))
                .andReturn();

        order = mapper.readValue(result.getResponse().getContentAsString(), PurchaseOrderDTO.class);

        assertThat(order.getLink("accept")).isNotNull();

        mockMvc.perform(post(order.getLink("accept").toString()))
                .andReturn();
    }
}
