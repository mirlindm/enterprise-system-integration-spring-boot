package com.example.demo.sales.rest;

import com.example.demo.DemoApplication;
import com.example.demo.common.application.ScheduledTasks;
import com.example.demo.inventory.domain.model.*;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.sales.domain.model.Invoice;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.InvoiceRepository;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class) // Check if the name of this class is correct or not
@WebAppConfiguration
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SalesRestControllerTests {
    @Autowired
    PlantInventoryEntryRepository entryRepository;

    @Autowired
    PlantInventoryItemRepository itemRepository;

    @Autowired
    PlantReservationRepository reservationRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    /* Covers requirement PS4 */
    @Test
    public void createPoShouldBeRejected() throws Exception{
        String now = LocalDate.now().toString();
        String tenDaysLater = LocalDate.now().plusDays(10).toString();

        // Create new entry with just one item and add reservation to it(should overlap with queried period)
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setDescription("Test entry");
        entryRepository.save(entry);

        PlantInventoryItem item = new PlantInventoryItem();
        item.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item.setPlantInfo(entry);
        itemRepository.save(item);

        PlantReservation pr = new PlantReservation();
        pr.setSchedule(BusinessPeriod.of(LocalDate.now().minusDays(5),LocalDate.now().plusDays(5)));
        pr.setPlant(item);
        reservationRepository.save(pr);

        // Create request body
        JSONObject requestBody = new JSONObject();

        JSONObject plantEntryJson = new JSONObject();
        plantEntryJson.put("_id",entry.getId().intValue());

        JSONObject rentalPeriodJson = new JSONObject();
        rentalPeriodJson.put("startDate",now);
        rentalPeriodJson.put("endDate",tenDaysLater);

        requestBody.put("plantEntry",plantEntryJson);
        requestBody.put("rentalPeriod",rentalPeriodJson);

        // "CreatePlantOrder" SHOULD BE REJECTED because there is no available item for given entry during given period
        MvcResult resultAfter = mockMvc.perform(post("/api/sales/po")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(requestBody.toString()))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertThat(resultAfter.getResponse().getStatus()).isEqualTo(400);
        assertThat(resultAfter.getResponse().getContentAsString()).contains("No available items");
    }

    /* Covers requirement PS4 */
    @Test
    public void createPoShouldBeAcceptedWithPendingStatus() throws Exception{
        String now = LocalDate.now().toString();
        String tenDaysLater = LocalDate.now().plusDays(10).toString();

        // Create new entry with just one item
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setDescription("Test entry description");
        entry.setPrice(BigDecimal.TEN);
        entry.setName("Test entry name");
        entryRepository.save(entry);

        PlantInventoryItem item = new PlantInventoryItem();
        item.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item.setPlantInfo(entry);
        item.setSerialNumber("test0001");
        itemRepository.save(item);

        // Create request body
        JSONObject requestBody = new JSONObject();

        JSONObject plantEntryJson = new JSONObject();
        plantEntryJson.put("_id",entry.getId().intValue());

        JSONObject rentalPeriodJson = new JSONObject();
        rentalPeriodJson.put("startDate",now);
        rentalPeriodJson.put("endDate",tenDaysLater);

        requestBody.put("plantEntry",plantEntryJson);
        requestBody.put("rentalPeriod",rentalPeriodJson);

        // "CreatePlantOrder" SHOULD BE ACCEPTED with status of "pending"
        MvcResult resultAfter = mockMvc.perform(post("/api/sales/po")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(requestBody.toString()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertThat(resultAfter.getResponse().getStatus()).isEqualTo(201);
        JSONObject respObj = new JSONObject(resultAfter.getResponse().getContentAsString());
        assertThat(respObj.has("status")).isTrue();
        assertThat(respObj.getString("status")).isNotNull();
        assertThat(respObj.getString("status")).isEqualTo("PENDING");
    }

    /* Covers requirement PS4 */
    @Test
    public void createPoShouldFailWithError() throws Exception{
        String now = LocalDate.now().toString();

        // Create new entry with just one item
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setDescription("Test entry description");
        entry.setPrice(BigDecimal.TEN);
        entry.setName("Test entry name");
        entryRepository.save(entry);

        PlantInventoryItem item = new PlantInventoryItem();
        item.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item.setPlantInfo(entry);
        item.setSerialNumber("test0001");
        itemRepository.save(item);

        // Create incomplete request body(without 'endDate')
        JSONObject requestBody = new JSONObject();

        JSONObject plantEntryJson = new JSONObject();
        plantEntryJson.put("_id",entry.getId().intValue());

        JSONObject rentalPeriodJson = new JSONObject();
        rentalPeriodJson.put("startDate",now);
        // endDate should be missing from request body

        requestBody.put("plantEntry",plantEntryJson);
        requestBody.put("rentalPeriod",rentalPeriodJson);

        // "CreatePlantOrder" SHOULD BE ACCEPTED with status of "pending"
        MvcResult resultAfter = mockMvc.perform(post("/api/sales/po")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(requestBody.toString()))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertThat(resultAfter.getResponse().getStatus()).isEqualTo(400);
        assertThat(resultAfter.getResponse().getContentAsString()).contains("endDate cannot be null");
    }

    /* Covers requirement PS4 */
    @Test
    public void createPoIgnoresUnserviceableItems() throws Exception{
        String now = LocalDate.now().toString();
        String tenDaysLater = LocalDate.now().plusDays(10).toString();

        // Create new entry with just one item
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setDescription("Test entry description");
        entry.setPrice(BigDecimal.TEN);
        entry.setName("Test entry name");
        entryRepository.save(entry);

        PlantInventoryItem item = new PlantInventoryItem();
        item.setEquipmentCondition(EquipmentCondition.UNSERVICEABLECONDEMNED);
        item.setPlantInfo(entry);
        item.setSerialNumber("test0001");
        itemRepository.save(item);

        // Create incomplete request body(without 'endDate')
        JSONObject requestBody = new JSONObject();

        JSONObject plantEntryJson = new JSONObject();
        plantEntryJson.put("_id",entry.getId().intValue());

        JSONObject rentalPeriodJson = new JSONObject();
        rentalPeriodJson.put("startDate",now);
        rentalPeriodJson.put("endDate",tenDaysLater);

        requestBody.put("plantEntry",plantEntryJson);
        requestBody.put("rentalPeriod",rentalPeriodJson);

        /* "CreatePlantOrder" SHOULD BE REJECTED because there is no available and serviceable item
                for given entry during given period */
        MvcResult resultAfter = mockMvc.perform(post("/api/sales/po")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(requestBody.toString()))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertThat(resultAfter.getResponse().getStatus()).isEqualTo(400);
        assertThat(resultAfter.getResponse().getContentAsString()).contains("No available items");
    }

    //Covers PS4 Requirement Acceptance
    @Test
    @Sql("/plants-dataset.sql")
    public void testPOAcceptFromPending() throws Exception{
        String uri = "/api/sales/po/{id}";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.PENDING);


        MvcResult mvcResult = mockMvc.perform(patch(uri,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("ACCEPTED");

        //Check Database after uri execution
        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.ACCEPTED);

        assertThat(reservationRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    @Sql("/plants-dataset.sql")
    public void testPOAcceptFromRejected() throws Exception{
        String uri = "/api/sales/po/{id}";
        PurchaseOrder po = purchaseOrderRepository.findById(2L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.REJECTED);


        MvcResult mvcResult = mockMvc.perform(patch(uri,2))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("PO cannot be accepted as it is not Pending");
        PurchaseOrder po2 = purchaseOrderRepository.findById(2L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.REJECTED);
    }

    /* Covers requirement PS5 */
    @Test
    @Sql("/plants-dataset.sql")
    public void fetchPurchaseOrderContainsStatus() throws Exception{
        String url = "/api/sales/po/{id}";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);

        MvcResult mvcResult = mockMvc.perform(get(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        JSONObject respObj = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertThat(respObj.has("status")).isTrue();
        assertThat(respObj.getString("status")).isEqualTo(po.getStatus().toString());
    }

    /* Covers requirement PS6 */
    @Test
    @Sql("/po-extend-dataset.sql")
    public void acceptPoExtension() throws Exception{
        String uri = "/api/sales/po/{id}/extend";

        //Check if PO exists and is accepted
        PurchaseOrder po = purchaseOrderRepository.findById(3L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.ACCEPTED);

        // Create request body
        JSONObject requestBody = new JSONObject();
        String fiveDaysLater = LocalDate.of(2020,07,25).toString();


        JSONObject rentalPeriodJson = new JSONObject();
        rentalPeriodJson.put("endDate",fiveDaysLater);
        requestBody.put("rentalPeriod",rentalPeriodJson);

        MvcResult mvcResult = mockMvc.perform(put(uri,3)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(requestBody.toString()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        //assertThat(status).isEqualTo(200);

        //String response = mvcResult.getResponse().getContentAsString();
        JSONObject respObj = new JSONObject(mvcResult.getResponse().getContentAsString());

        System.out.println(respObj);
        assertThat(respObj.getString("status")).isEqualTo("ACCEPTED");
        assertThat(respObj.getString("total")).isNotEqualTo(1000);
        assertThat(respObj.getString("rentalPeriod")).isNotEqualTo(po.getRentalPeriod());

    }


    /* Covers requirement PS6 */
    @Test
    @Sql("/po-extend-dataset.sql")
    public void rejectPoExtension() throws Exception{
        String uri = "/api/sales/po/{id}/extend";


        //Check if PO exists and is accepted
        PurchaseOrder po = purchaseOrderRepository.findById(3L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.ACCEPTED);

        // Create request body
        JSONObject requestBody = new JSONObject();
        String fiveDaysLater = LocalDate.of(2020,07,28).toString();


        JSONObject rentalPeriodJson = new JSONObject();
        rentalPeriodJson.put("endDate",fiveDaysLater);
        requestBody.put("rentalPeriod",rentalPeriodJson);

        MvcResult mvcResult = mockMvc.perform(put(uri,3)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(requestBody.toString()))
                .andExpect(status().is4xxClientError())
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        //assertThat(status).isEqualTo(400);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("Your PO request cannot be extended");

    }



    /* Covers requirement PS7 */
    @Test
    @Sql("/plants-dataset.sql")
    public void cancellationSucceedsWhenPending() throws Exception{
        String url = "/api/sales/po/{id}/cancel";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.PENDING);

        MvcResult mvcResult = mockMvc.perform(delete(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        JSONObject respObj = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertThat(respObj.has("status")).isTrue();
        assertThat(respObj.getString("status")).isEqualTo("CANCELLED");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.CANCELLED);
    }


    /* Covers requirement PS7 */
    @Test
    @Sql("/plants-dataset.sql")
    public void cancellationSucceedsWhenAccepted() throws Exception{
        String url = "/api/sales/po/{id}/cancel";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        po.setStatus(POStatus.ACCEPTED);
        purchaseOrderRepository.save(po);

        MvcResult mvcResult = mockMvc.perform(delete(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        JSONObject respObj = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertThat(respObj.has("status")).isTrue();
        assertThat(respObj.getString("status")).isEqualTo("CANCELLED");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.CANCELLED);
    }

    /* Covers requirement PS7 */
    @Test
    @Sql("/plants-dataset.sql")
    public void cancellationFails() throws Exception{
        String url = "/api/sales/po/{id}/cancel";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        po.setStatus(POStatus.PLANT_DISPATCHED);
        purchaseOrderRepository.save(po);

        MvcResult mvcResult = mockMvc.perform(delete(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("Cancellation not allowed");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.PLANT_DISPATCHED);
    }

    /* Covers requirement PS8 */
    @Test
    @Sql("/plants-dataset.sql")
    public void markAsDispatchedShouldFail() throws Exception{
        String url = "/api/sales/po/{id}/plant_dispatched";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.PENDING);

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("Current status of purchase order does not allow dispatching");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.PENDING);
    }

    /* Covers requirement PS8 */
    @Test
    @Sql("/plants-dataset.sql")
    public void markAsDispatchedShouldSucceed() throws Exception{
        String url = "/api/sales/po/{id}/plant_dispatched";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        po.setStatus(POStatus.ACCEPTED);
        purchaseOrderRepository.save(po);

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        JSONObject respObj = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertThat(respObj.has("status")).isTrue();
        assertThat(respObj.getString("status")).isEqualTo("PLANT_DISPATCHED");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.PLANT_DISPATCHED);
    }

    /* Covers requirement PS9 */
    @Test
    @Sql("/plants-dataset.sql")
    public void markAsDeliveredShouldFail() throws Exception{
        String url = "/api/sales/po/{id}/plant_delivered";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.PENDING);

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("Current status of purchase order does not allow marking it as 'DELIVERED'");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.PENDING);
    }

    /* Covers requirement PS9 */
    @Test
    @Sql("/plants-dataset.sql")
    public void markAsDeliveredShouldSucceed() throws Exception{
        String url = "/api/sales/po/{id}/plant_delivered";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        po.setStatus(POStatus.PLANT_DISPATCHED);
        purchaseOrderRepository.save(po);

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        JSONObject respObj = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertThat(respObj.has("status")).isTrue();
        assertThat(respObj.getString("status")).isEqualTo("PLANT_DELIVERED");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.PLANT_DELIVERED);
    }

    /* Covers requirement PS9 */
    @Test
    @Sql("/plants-dataset.sql")
    public void markAsRejectedByCustomerShouldFail() throws Exception{
        String url = "/api/sales/po/{id}/plant_rejected";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.PENDING);

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("Current status of purchase order does not allow marking it as 'REJECTED'");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.PENDING);
    }

    /* Covers requirement PS9 */
    @Test
    @Sql("/plants-dataset.sql")
    public void markAsRejectedByCustomerShouldSucceed() throws Exception{
        String url = "/api/sales/po/{id}/plant_rejected";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        po.setStatus(POStatus.PLANT_DISPATCHED);
        purchaseOrderRepository.save(po);

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        JSONObject respObj = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertThat(respObj.has("status")).isTrue();
        assertThat(respObj.getString("status")).isEqualTo("PLANT_REJECTED");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.PLANT_REJECTED);
    }

    /* Covers requirement PS10 */
    @Test
    @Sql("/plants-dataset.sql")
    public void markAsReturnedByCustomerShouldFail() throws Exception{
        String url = "/api/sales/po/{id}/plant_returned";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.PENDING);

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("Current status of purchase order does not allow marking it as 'RETURNED'");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.PENDING);
    }

    /* Covers requirement PS10 */
    @Test
    @Sql("/plants-dataset.sql")
    public void markAsReturnedShouldSucceed() throws Exception{
        String url = "/api/sales/po/{id}/plant_returned";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        po.setStatus(POStatus.PLANT_DELIVERED);
        purchaseOrderRepository.save(po);

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        JSONObject respObj = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertThat(respObj.has("status")).isTrue();
        assertThat(respObj.getString("status")).isEqualTo("PLANT_RETURNED");

        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.PLANT_RETURNED);
    }

    //Covers PS4 Requirement Rejection
    @Test
    @Sql("/plants-dataset.sql")
    public void testPOReject() throws Exception{
        String uri = "/api/sales/po/{id}";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po.getStatus()).isEqualTo(POStatus.PENDING);


        MvcResult mvcResult = mockMvc.perform(delete(uri,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("REJECTED");

        //Check Database after uri execution
        PurchaseOrder po2 = purchaseOrderRepository.findById(1L).orElse(null);
        assertThat(po2.getStatus()).isEqualTo(POStatus.REJECTED);

        assertThat(reservationRepository.findAll().size()).isEqualTo(0);
    }

    /* Covers requirement PS13 */
    @Test
    @Sql("/plants-dataset.sql")
    public void submitInvoiceForReturnedPlants() throws Exception{
        String url = "/api/sales/po/{id}/plant_returned";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        po.setStatus(POStatus.PLANT_DELIVERED);
        purchaseOrderRepository.save(po);
        int totalInvoiceCount = invoiceRepository.findAll().size();

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        JSONObject respObj = new JSONObject(mvcResult.getResponse().getContentAsString());

        int totalInvoiceCountUpdated = invoiceRepository.findAll().size();
        assertThat(totalInvoiceCountUpdated).isEqualTo(totalInvoiceCount+1);
    }

    /* Covers requirement PS15 */
    @Test
    @Sql("/plants-dataset.sql")
    public void remittanceAdviceIsRejected() throws Exception{
        String url = "/api/sales/po/{id}/remittance";
        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);
        po.setStatus(POStatus.PLANT_DELIVERED);
        purchaseOrderRepository.save(po);


        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        assertThat(mvcResult.getResponse().getContentAsString()).contains("Plant has not been returned yet");
    }

    /* Covers requirement PS15 */
    @Test
    @Sql("/plants-dataset.sql")
    public void remittanceAdviceIsAccepted() throws Exception{
        String url = "/api/sales/po/{id}/remittance";

        PurchaseOrder po = purchaseOrderRepository.findById(1L).orElse(null);

        Invoice invoice = new Invoice();
        invoice.setPurchaseOrder(po);
        invoiceRepository.save(invoice);

        po.setStatus(POStatus.PLANT_RETURNED);
        po.setInvoice(invoice);
        purchaseOrderRepository.save(po);

        MvcResult mvcResult = mockMvc.perform(patch(url,1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("REMITTANCE ADVICE RECEIVED");

        PurchaseOrder poUpdated = purchaseOrderRepository.findById(po.getId()).orElse(null);
        assertThat(poUpdated.getInvoice().getIsPaid()).isTrue();
    }

    /* Covers requirement PS14. */
    @Test
    public void systemRemindsTwoDaysBefore() throws Exception{
        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now().plusDays(2));
        invoiceRepository.save(invoice);

        List<Invoice> remindedInvoices = ScheduledTasks.remindUnpaidInvoices();

        assertThat(remindedInvoices).contains(invoice);
    }

    /* Covers requirement PS14. */
    @Test
    public void systemRemindsFiveDaysBefore() throws Exception{
        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now().plusDays(5));
        invoiceRepository.save(invoice);

        List<Invoice> remindedInvoices = ScheduledTasks.remindUnpaidInvoices();

        assertThat(remindedInvoices).contains(invoice);
    }

    /* Covers requirement PS14. */
    @Test
    public void systemRemindsTenDaysBefore() throws Exception{
        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now().plusDays(10));
        invoiceRepository.save(invoice);

        List<Invoice> remindedInvoices = ScheduledTasks.remindUnpaidInvoices();

        assertThat(remindedInvoices).contains(invoice);
    }

    /* Covers requirement PS14. */
    @Test
    public void systemDoesntRemindWhenNotNecessary() throws Exception{
        // Does not remind where number of days to due date is not 2,5 or 10
        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now().plusDays(7));
        invoiceRepository.save(invoice);

        List<Invoice> remindedInvoices = ScheduledTasks.remindUnpaidInvoices();

        assertThat(remindedInvoices).doesNotContain(invoice);
    }

    /* Covers requirement PS14. */
    @Test
    public void systemDoesntRemindPaidInvoices() throws Exception{
        // Does not remind where number of days to due date is not 2,5 or 10

        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now().plusDays(5));
        invoice.setIsPaid(true);
        invoiceRepository.save(invoice);

        List<Invoice> remindedInvoices = ScheduledTasks.remindUnpaidInvoices();

        assertThat(remindedInvoices).doesNotContain(invoice);
    }



}
