package com.example.demo.rest;

import com.example.demo.DemoApplication;
import com.example.demo.common.application.MailHelper;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
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


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class) // Check if the name of this class is correct or not
@WebAppConfiguration
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class MaintenanceRestControllerTests {
    protected MockMvc mvc;

    @Autowired
    PlantInventoryEntryRepository entryRepo;

    @Autowired
    PlantInventoryItemRepository itemRepo;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired //@Qualifier("_halObjectMapper")
            ObjectMapper mapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    // Covers PS11
    @Test
    public void insertMaintenanceTask_t1() throws Exception {
        String uri = "/api/maintenance/items/{id}/tasks/create";

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

    // Covers PS11
    // Below test checks the creation of a new Maintenance Task with start and end dates in the past
    // which is expected to fail
    @Test
    public void insertMaintenanceTask_t2() throws Exception {
        String uri = "/api/maintenance/items/{id}/tasks/create";

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

    // Covers PS11
    //Testing the end_date < start_date
    @Test
    public void insertMaintenanceTask_t3() throws Exception {
        String uri = "/api/maintenance/items/{id}/tasks/create";

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

    // Covers PS11
    //Testing with same start_date and end_date
    @Test
    public void insertMaintenanceTask_t7() throws Exception {
        String uri = "/api/maintenance/items/{id}/tasks/create";

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

    // Covers PS11
    //checking if total is negative - should fail
    @Test
    public void insertMaintenanceTask_t4() throws Exception {
        String uri = "/api/maintenance/items/{id}/tasks/create";

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

    // Covers PS11
    //total can be zero and maintenance task can be created as such
    @Test
    public void insertMaintenanceTask_t8() throws Exception {
        String uri = "/api/maintenance/items/{id}/tasks/create";

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

    // Covers PS11
    // Successful creation of maintenance task
    @Test
    public void insertMaintenanceTask_t9() throws Exception {
        String uri = "/api/maintenance/items/{id}/tasks/create";

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

        MvcResult mvcResult = mockMvc.perform(post(uri,5)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(201);
        String response = mvcResult.getResponse().getContentAsString();
    }


    // Covers PS12
    // 1) Maintenance Task is created
    // 2) Next purchase order is cancelled, because no available item left
    // 3) Customer is notified
    @Test
    public void insertMaintenanceTaskCancelsNextOrder() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(10);
        BusinessPeriod maintTaskPeriod = BusinessPeriod.of(startDate,endDate);

        // Create new entry
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setName("Test entry");
        entry.setDescription("Test entry description");
        entry.setPrice(BigDecimal.TEN);
        entryRepo.save(entry);
        System.out.println("Entry created with ID "+entry.getId());

        // Create items of new entry
        PlantInventoryItem item = new PlantInventoryItem();
        item.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item.setPlantInfo(entry);
        item.setSerialNumber("random_serial_number");
        itemRepo.save(item);

        // Add purchase order to the entry that overlaps with maintenance task
        PurchaseOrder po = new PurchaseOrder();
        po.setRentalPeriod(maintTaskPeriod);
        po.setPlantEntry(entry);
        purchaseOrderRepository.save(po);

        String url = "/api/maintenance/items/{id}/tasks/create";
        JSONObject req = new JSONObject();
        req.put("description","random_desc");
        req.put("type_of_work","CORRECTIVE");
        req.put("total","100");
        req.put("plant_id",entry.getId());

        JSONObject rentalPeriod = new JSONObject();
        rentalPeriod.put("startDate",startDate);
        rentalPeriod.put("endDate",endDate);
        req.put("rentalPeriod",rentalPeriod);

        MvcResult mvcResult = mockMvc.perform(post(url,item.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(req.toString()))
                .andReturn();


        int status = mvcResult.getResponse().getStatus();
        System.out.println("===> status is "+status);
        assertThat(status).isEqualTo(201);

        PurchaseOrder poUpdated = purchaseOrderRepository.findById(po.getId()).orElse(null);
        assertThat(poUpdated.getIsCancelledDueToMaintenance()).isTrue();
        assertThat(poUpdated.getStatus()).isEqualTo(POStatus.CANCELLED);
    }

    // Covers PS12
    // 1) Maintenance Task is created
    // 2) No PO is cancelled, because there are other availabe item(s)
    @Test
    public void insertMaintenanceTaskDoesntCancelOtherOrders() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(10);
        BusinessPeriod maintTaskPeriod = BusinessPeriod.of(startDate,endDate);

        // Create new entry
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setName("Test entry");
        entry.setDescription("Test entry description");
        entry.setPrice(BigDecimal.TEN);
        entryRepo.save(entry);
        System.out.println("Entry created with ID "+entry.getId());

        // Create items of new entry
        PlantInventoryItem item1 = new PlantInventoryItem();
        item1.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item1.setPlantInfo(entry);
        item1.setSerialNumber("random_serial_number");
        itemRepo.save(item1);

        PlantInventoryItem item2 = new PlantInventoryItem();
        item2.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item2.setPlantInfo(entry);
        item2.setSerialNumber("random_serial_number");
        itemRepo.save(item2);

        // Add purchase order to the entry that overlaps with maintenance task
        PurchaseOrder po = new PurchaseOrder();
        po.setRentalPeriod(maintTaskPeriod);
        po.setPlantEntry(entry);
        purchaseOrderRepository.save(po);

        String url = "/api/maintenance/items/{id}/tasks/create";
        JSONObject req = new JSONObject();
        req.put("description","random_desc");
        req.put("type_of_work","CORRECTIVE");
        req.put("total","100");
        req.put("plant_id",entry.getId());

        JSONObject rentalPeriod = new JSONObject();
        rentalPeriod.put("startDate",startDate);
        rentalPeriod.put("endDate",endDate);
        req.put("rentalPeriod",rentalPeriod);

        MvcResult mvcResult = mockMvc.perform(post(url,item1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(req.toString()))
                .andReturn();


        int status = mvcResult.getResponse().getStatus();
        System.out.println("===> status is "+status);
        assertThat(status).isEqualTo(201);

        PurchaseOrder poUpdated = purchaseOrderRepository.findById(po.getId()).orElse(null);
        assertThat(poUpdated.getIsCancelledDueToMaintenance()).isFalse();
        assertThat(poUpdated.getStatus()).isNotEqualTo(POStatus.CANCELLED);
    }
}
