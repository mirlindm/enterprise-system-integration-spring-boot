package com.example.demo.rest;

import com.example.demo.DemoApplication;
import com.example.demo.inventory.domain.model.*;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.maintenance.domain.repository.MaintenanceTaskRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.rest.InventoryRestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.tomcat.jni.Local;
import org.h2.table.Plan;
import org.json.JSONArray;
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
import static org.assertj.core.api.InstanceOfAssertFactories.STRING;
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
    PlantReservationRepository reservationRepository;

    @Autowired
    PlantInventoryEntryRepository entryRepository;

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

    /* Covers requirement PS1(The system should allow a customer to list the available plants and their prices) */
    @Test
    public void getAvailablePlantsContainsPrice() throws Exception{
        String now = LocalDate.now().toString();
        String tenDaysLater = LocalDate.now().plusDays(10).toString();

        MvcResult result = mockMvc.perform(
                get("/api/plants/available?startDate={start}&endDate={end}", now, tenDaysLater))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        JSONArray jsonArray = new JSONArray(result.getResponse().getContentAsString());

        for(int i=0;i<jsonArray.length();i++){
            JSONObject entryResp = jsonArray.getJSONObject(i);
            assertThat(entryResp.has("price")).isTrue();
            assertThat(entryResp.getDouble("price")).isNotNull();
            assertThat(entryResp.getDouble("price")).isNotNaN();
        }
    }

    /* Covers requirement PS1(The system should allow a customer to list the available plants and their prices) */
    @Test
    public void getAvailablePlantsDoesNotIncludeReservedItems() throws Exception{
        String now = LocalDate.now().toString();
        String tenDaysLater = LocalDate.now().plusDays(10).toString();

        // Create new entry with just one item
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setDescription("Test entry");
        entryRepository.save(entry);

        PlantInventoryItem item = new PlantInventoryItem();
        item.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item.setPlantInfo(entry);
        itemRepository.save(item);

        // "getAvailablePlants" should include created entry
        MvcResult resultBefore = mockMvc.perform(
                get("/api/plants/available?startDate={start}&endDate={end}", now, tenDaysLater))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(resultBefore.getResponse().getStatus()).isEqualTo(200);
        assertThat(resultBefore.getResponse().getContentAsString()).contains(entry.getDescription());

        // Add reservation to the one and only item of entry. Schedule should overlap with queried period
        PlantReservation pr = new PlantReservation();
        pr.setSchedule(BusinessPeriod.of(LocalDate.now().minusDays(5),LocalDate.now().plusDays(5)));
        pr.setPlant(item);
        reservationRepository.save(pr);

        // "getAvailablePlants" SHOULD NOT include created entry as it doesn't have free item for queried period
        MvcResult resultAfter = mockMvc.perform(
                get("/api/plants/available?startDate={start}&endDate={end}", now, tenDaysLater))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(resultAfter.getResponse().getStatus()).isEqualTo(200);
        assertThat(resultAfter.getResponse().getContentAsString()).doesNotContain(entry.getDescription());
    }

    /* Covers requirement PS1(The system should allow a customer to list the available plants and their prices) */
    @Test
    public void getAvailablePlantsIncludesOnlyServiceable() throws Exception{
        String now = LocalDate.now().toString();
        String tenDaysLater = LocalDate.now().plusDays(10).toString();

        // Create new entry with just one item
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setDescription("Test entry");
        entryRepository.save(entry);

        PlantInventoryItem item = new PlantInventoryItem();
        item.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item.setPlantInfo(entry);
        itemRepository.save(item);

        // "getAvailablePlants" SHOULD include created entry
        MvcResult resultBefore = mockMvc.perform(
                get("/api/plants/available?startDate={start}&endDate={end}", now, tenDaysLater))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(resultBefore.getResponse().getStatus()).isEqualTo(200);
        assertThat(resultBefore.getResponse().getContentAsString()).contains(entry.getDescription());

        // Change condition of one and only item of the entry
        item.setEquipmentCondition(EquipmentCondition.UNSERVICEABLEINCOMPLETE);
        itemRepository.save(item);

        // "getAvailablePlants" SHOULD NOT include created entry
        MvcResult resultAfter = mockMvc.perform(
                get("/api/plants/available?startDate={start}&endDate={end}", now, tenDaysLater))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(resultAfter.getResponse().getStatus()).isEqualTo(200);
        assertThat(resultAfter.getResponse().getContentAsString()).doesNotContain(entry.getDescription());
    }

    /* Covers requirement PS2(The system should allow a customer to check the price for a
            given plant (given the plant identifier)) */
    @Test
    public void getPlantEntryContainsPrice() throws Exception{
        // Create new entry with just one item
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setDescription("Test entry");
        entry.setPrice(BigDecimal.ONE);
        entryRepository.save(entry);

        PlantInventoryItem item = new PlantInventoryItem();
        item.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item.setPlantInfo(entry);
        itemRepository.save(item);

        // Response should include correct price of the entry
        MvcResult resultBefore = mockMvc.perform(
                get("/api/plants/{id}", entry.getId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(resultBefore.getResponse().getStatus()).isEqualTo(200);
        JSONObject entryResp = new JSONObject(resultBefore.getResponse().getContentAsString());
        assertThat(entryResp.has("price")).isTrue();
        assertThat(entryResp.getDouble("price")).isNotNull();
        assertThat(entryResp.getDouble("price")).isNotNaN();
        assertThat(entryResp.getDouble("price")).isEqualTo(entry.getPrice().doubleValue());
    }

    /* Covers requirement PS3(The system should allow a customer to check the availability
            of a given plant during a given time period) */
    @Test
    public void getPlantEntryWillReturnUnavailable() throws Exception{
        String url = "/api/plants/{id}/availability?startDate={startDate}&endDate={endDate}";
        String now = LocalDate.now().toString();
        String tenDaysLater = LocalDate.now().plusDays(10).toString();

        // Create new entry with one SERVICEABLE and one NOT SERVICEABLE item
        PlantInventoryEntry entry = new PlantInventoryEntry();
        entry.setDescription("Test entry");
        entryRepository.save(entry);

        PlantInventoryItem item = new PlantInventoryItem();
        item.setEquipmentCondition(EquipmentCondition.SERVICEABLE);
        item.setPlantInfo(entry);
        itemRepository.save(item);

        PlantInventoryItem item2 = new PlantInventoryItem();
        item2.setEquipmentCondition(EquipmentCondition.UNSERVICEABLECONDEMNED);
        item2.setPlantInfo(entry);
        itemRepository.save(item2);

        // "checkAvailability" SHOULD return true as there is one free and SERVICEABLE item
        MvcResult resultBefore = mockMvc.perform(
                get(url, entry.getId(), now, tenDaysLater))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(resultBefore.getResponse().getStatus()).isEqualTo(200);
        JSONObject entryResp = new JSONObject(resultBefore.getResponse().getContentAsString());
        assertThat(entryResp.has("available")).isTrue();
        assertThat(entryResp.getBoolean("available")).isNotNull();
        assertThat(entryResp.getBoolean("available")).isTrue();

        // Add reservation to the only SERVICEABLE item of entry
        PlantReservation reservation = new PlantReservation();
        reservation.setPlant(item);
        reservation.setSchedule(BusinessPeriod.of(LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5)));
        reservationRepository.save(reservation);

        // "checkAvailability" SHOULD return false as there isn't any free and SERVICEABLE item for the queried period
        MvcResult resultAfter = mockMvc.perform(
                get(url, entry.getId(), now, tenDaysLater))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(resultAfter.getResponse().getStatus()).isEqualTo(200);
        JSONObject entryRespAfter = new JSONObject(resultAfter.getResponse().getContentAsString());
        assertThat(entryRespAfter.has("available")).isTrue();
        assertThat(entryRespAfter.getBoolean("available")).isNotNull();
        assertThat(entryRespAfter.getBoolean("available")).isFalse();
    }
}


