package com.example.demo.inventory.rest;

import com.example.demo.DemoApplication;
import com.example.demo.inventory.application.dto.MaintenanceTaskDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.*;
import com.example.demo.inventory.domain.repository.MaintenanceTaskRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired @Qualifier("_halObjectMapper")
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

    @Test
    public void insertMaintenanceTask_t1() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"PREVENTIVE\",\n" +
                "        \"total\": 1000.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 1\n" +
                "    }";
        System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(201);

        String response = mvcResult.getResponse().getContentAsString();

    }

    //Below test checks the creation of a new Maintenance Task with start and end dates in the past
    //which is expected to fail
    @Test
    public void insertMaintenanceTask_t2() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"PREVENTIVE\",\n" +
                "        \"total\": 1000.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2019-08-10\",\n" +
                "            \"endDate\": \"2019-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 1\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);
        //test passes, since maintenance can't be created in the past, hence the status code for the request is 400
        String response1 = mvcResult.getResponse().getContentAsString();
        assertThat(response1).contains("endDate must be in the future");
        String response2 = mvcResult.getResponse().getContentAsString();
        assertThat(response1).contains("startDate must be in the future");


    }

    //Testing the end_date < start_date
    @Test
    public void insertMaintenanceTask_t3() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"PREVENTIVE\",\n" +
                "        \"total\": 1000.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-20\",\n" +
                "            \"endDate\": \"2020-08-10\"\n" +
                "        },\n" +
                "        \"plant_id\": 1\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("startDate must be before endDate");
    }

    //Testing with same start_date and end_date
    @Test
    public void insertMaintenanceTask_t7() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"PREVENTIVE\",\n" +
                "        \"total\": 1000.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-10\"\n" +
                "        },\n" +
                "        \"plant_id\": 1\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("startDate must be before endDate");
        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

    }

    //checking if total is negative - should fail
    @Test
    public void insertMaintenanceTask_t4() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"PREVENTIVE\",\n" +
                "        \"total\": -42.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 1\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("Total amount cannot be negative");


    }

    //total can be zero and maintenance task can be created as such
    @Test
    public void insertMaintenanceTask_t8() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"PREVENTIVE\",\n" +
                "        \"total\": 0.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 1\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(201);
        String response = mvcResult.getResponse().getContentAsString();



    }

    //Testing with a WorkType of Corrective
    @Test
    public void insertMaintenanceTask_t5() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"CORRECTIVE\",\n" +
                "        \"total\": -42.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 1\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("CORRECTIVE maintenance tasks only can be scheduled for " +
                "UNSERVICEABLE REPAIRABLE and INCOMPLETE plants");
    }

    //Testing with a WorkType of Operative with UNSERVICEABLECONDEMNED
    @Test
    public void insertMaintenanceTask_t9() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                    "        \"type_of_work\": \"OPERATIVE\",\n" +
                "        \"total\": -42.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 4\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,4)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("OPERATIONAL maintenance tasks can be scheduled for ANY plant, except for the UNSERVICEABLE CONDEMNED ones");
    }

    //Testing with a WorkType of PREVENTIVE with UNSERVICEABLEREPAIRABLE
    @Test
    public void insertMaintenanceTask_t10() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"PREVENTIVE\",\n" +
                "        \"total\": 42.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 5\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,5)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("A PREVENTIVE maintenance task only can be scheduled for SERVICEABLE plants");
    }

    //Testing with a WorkType of OPERATIVE with UNSERVICEABLEINCOMPLETE
    @Test
    public void insertMaintenanceTask_t11() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"OPERATIVE\",\n" +
                "        \"total\": 42.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 6\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,6)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(201);
        String response = mvcResult.getResponse().getContentAsString();
        //assertThat(response).contains("A PREVENTIVE maintenance task only can be scheduled for SERVICEABLE plants");
    }

    //Testing with a WorkType of OPERATIVE with UNSERVICEABLEREPAIRABLE
    @Test
    public void insertMaintenanceTask_t12() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"OPERATIVE\",\n" +
                "        \"total\": 42.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 5\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,5)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(201);
        String response = mvcResult.getResponse().getContentAsString();
        //assertThat(response).contains("A PREVENTIVE maintenance task only can be scheduled for SERVICEABLE plants");
    }

    //Testing with a WorkType of CORRECIVE with UNSERVICEABLEREPAIRABLE
    @Test
    public void insertMaintenanceTask_t13() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"CORRECTIVE\",\n" +
                "        \"total\": 42.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 5\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,5)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(201);
        String response = mvcResult.getResponse().getContentAsString();
        //assertThat(response).contains("A PREVENTIVE maintenance task only can be scheduled for SERVICEABLE plants");
    }



    //Testing with typeWork of OPERATIVE
    @Test
    public void insertMaintenanceTask_t6() throws Exception {
        String uri = "/api/plants/items/{id}/maintenance_tasks/create";

        String newJson = "{\n" +
                "        \"_id\": 1,\n" +
                "        \"description\": \"first maintenance task\",\n" +
                "        \"type_of_work\": \"OPERATIVE\",\n" +
                "        \"total\": 42.00,\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        },\n" +
                "        \"plant_id\": 1\n" +
                "    }";
        //System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(201);
        String response = mvcResult.getResponse().getContentAsString();

    }



}

