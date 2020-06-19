package com.example.demo.sales.rest;

import com.example.demo.DemoApplication;
import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@DirtiesContext
public class SalesRestControllerTests {
//    @Autowired
//    PlantInventoryEntryRepository repo;
//
//    @Autowired
//    private WebApplicationContext wac;
//    private MockMvc mockMvc;
//
//    @Autowired @Qualifier("_halObjectMapper")
//    ObjectMapper mapper;
//
//    @Before
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//    }
//
//    @Test
//    @Sql("/plants-dataset.sql")
//    public void testGetAllPlants() throws Exception {
//        MvcResult result = mockMvc.perform(get("/api/sales/plants?name=Exc&startDate=2019-04-14&endDate=2019-04-25"))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Location", isEmptyOrNullString()))
//                .andReturn();
//
//        List<PlantInventoryEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PlantInventoryEntryDTO>>() { });
//
//        assertThat(plants.size()).isEqualTo(2); //****** the actual value is 0
//
//        PurchaseOrderDTO order = new PurchaseOrderDTO();
//        order.setPlant(plants.get(1));
//        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.of(2019, 4, 14), LocalDate.of(2019, 4, 25)));
//
//        mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());
//    }
//

        @Autowired
    PlantInventoryEntryRepository repo;

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired @Qualifier("_halObjectMapper")
    ObjectMapper mapper;

        @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    @Test
//    @Sql("file:plants-dataset.sql")
    @Sql("/plants-dataset.sql")
    public void testPurchaseOrderAcceptance() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/inventory/plants?name=Exc&startDate=2016-03-14&endDate=2016-03-25"))
                .andReturn();
        List<PlantInventoryEntryDTO> plants =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<PlantInventoryEntryDTO>>() { });

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plants.get(2));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now()));

        result = mockMvc.perform(post("/api/sales/orders")
                .content(mapper.writeValueAsString(order))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", not(isEmptyOrNullString())))
                .andReturn();

        order = mapper.readValue(result.getResponse().getContentAsString(), PurchaseOrderDTO.class);

//        assertThat(order.get_xlink("accept"), is(notNullValue()));
//
//        mockMvc.perform(post(order.get_xlink("accept").getHref()))
//                .andReturn();
    }
}
