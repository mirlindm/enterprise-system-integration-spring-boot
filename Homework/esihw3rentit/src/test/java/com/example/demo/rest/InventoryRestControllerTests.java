package com.example.demo.rest;

import com.example.demo.DemoApplication;
import com.example.demo.inventory.domain.model.*;
import com.example.demo.maintenance.domain.repository.MaintenanceTaskRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.rest.InventoryRestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class) // Check if the name of this class is correct or not
@WebAppConfiguration
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class InventoryRestControllerTests {
    protected MockMvc mvc;
    @Autowired
    PlantInventoryEntryRepository repo;

    @Autowired
    InventoryRestController cont;

    @Autowired
    PlantInventoryItemRepository itemRepository;

    @Autowired
    MaintenanceTaskRepository mtRepository;

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired //@Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void updatePlantEQConditionCorrect() throws Exception {
        String uri = "/api/plants/items/{id}";
        PlantInventoryItem item = itemRepository.findById(1L).orElse(null);
        assertThat(item.getEquipmentCondition()).isEqualTo(EquipmentCondition.SERVICEABLE);
        item.setEquipmentCondition(EquipmentCondition.UNSERVICEABLEREPAIRABLE);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String inputJson =  ow.writeValueAsString(item);
        System.out.println(inputJson);

        MvcResult mvcResult = mockMvc.perform(put(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(inputJson))
                .andReturn();
//        System.out.println("========================================");
//        System.out.println(mvcResult.getResponse().getContentAsString());
//        System.out.println(mvcResult.getRequest().getContentAsString());
//        System.out.println("========================================");

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("UNSERVICEABLEREPAIRABLE");

        PlantInventoryItem item2 = itemRepository.findById(1L).orElse(null);
        assertThat(item2.getEquipmentCondition()).isEqualTo(EquipmentCondition.UNSERVICEABLEREPAIRABLE);

    }





}


