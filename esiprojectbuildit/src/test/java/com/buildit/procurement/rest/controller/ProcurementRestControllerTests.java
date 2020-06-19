package com.buildit.procurement.rest.controller;

import com.buildit.Auth.application.service.TokenProvider;
import com.buildit.BuilditApplication;
import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.maintenance.domain.repositories.PlantInventoryItemRepository;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.application.service.PlantHiringService;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.procurement.rest.ProcurementRestController;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.services.RentalService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {BuilditApplication.class})
@WebAppConfiguration
//@Sql(scripts="/plantHire.sql")
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //added
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) //added
public class ProcurementRestControllerTests {
    protected MockMvc mvc; //added

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired //@Qualifier("_halObjectMapper")
            ObjectMapper mapper;

    @Autowired
    RentalService rentalService;

    @Autowired
    PlantHiringService plantHiringService;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    @Autowired
    PlantHireRequestRepository plantHireRequestRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    //private ProcurementRestController procurementRestController;


//    @Configuration
//    static class RentalServiceMock {
//        @Bean
//        public RentalService rentalService() {
//            return Mockito.mock(RentalService.class);
//        }
//    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
    }

//    @Test
//    public void testGetAllPlants() throws Exception {
//        Resource responseBody = new ClassPathResource("trucks.json", this.getClass());
//        List<PlantInventoryEntryDTO> list =
//                mapper.readValue(responseBody.getFile(), new TypeReference<List<PlantInventoryEntryDTO>>() { });
//        LocalDate startDate = LocalDate.now();
//        LocalDate endDate = startDate.plusDays(2);
//        //when(rentalService.findAvailablePlants("Truck", startDate, endDate)).thenReturn(list);
//        when(rentalService.findAvailablePlants("",startDate, endDate)).thenReturn(list);
//
//        String accessToken = tokenProvider.generateToken(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user1","password1")));
//        System.out.println(accessToken);
//        MvcResult result = mockMvc.perform(
//                get("/api/procurements/plants?startDate={start}&endDate={end}", startDate, endDate)
//                        .header("Authorization", "Bearer " + accessToken)
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Add test expectations
//    }

    //    Covers requirement CC1 (Create Plant Hire Request with Success)
    @Test
    @WithMockUser(roles="ADMIN")
    public void createPlantHireRequest_success() throws Exception {
        String uri = "/api/procurements/hire";

        String newJson = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(201);

        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response.contains("Ahmed"));
        assertThat(response.contains("Site 1"));
        assertThat(response.contains("Test Entry"));
        assertThat(response.contains("PENDING"));

    }

    //    Covers requirement CC1 (Create Plant Hire Request with invalid data (entryID) - should fail)
    @Test
    @WithMockUser(roles="ADMIN")
    public void createPlantHireRequest_failure() throws Exception {
        String uri = "/api/procurements/hire";

        String newJson = "{\n" +
                "        \"entryId\":  \"ID1\",\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isNotEqualTo(201);
        assertThat(status).isEqualTo(400);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response.contains("not a valid Long value"));
    }

    //    Covers requirement CC2 (Update Plant Hire Request Successfully)
    @Test
    @WithMockUser(roles="ADMIN")
    public void updatePlantHireRequest_success() throws Exception {
        String post_uri = "/api/procurements/hire";

        String newJson1 = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson1);

        MvcResult post_mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson1)).andExpect(status().is2xxSuccessful())
                .andReturn();

        int status1 = post_mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);

        String response1 = post_mvcResult.getResponse().getContentAsString();
        assertThat(response1.contains("Ahmed"));


        String update_uri = "/api/procurements/hire/{id}";

        String newJson2 = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"New Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Mirlind\",\n" +
                "        \"nameOfConstructionSite\": \"Site 2\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson2);

        MvcResult update_mvcResult = mockMvc.perform(put(update_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson2)).andExpect(status().is2xxSuccessful())
                .andReturn();

        int status2 = update_mvcResult.getResponse().getStatus();
        assertThat(status2).isEqualTo(200);

        String response2 = update_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("Mirlind"));

    }

    //    Covers requirement CC2 (Update Plant Hire Request With Invalid Data - Fail)
    @Test
    @WithMockUser(roles="ADMIN")
    public void updatePlantHireRequest_failure() throws Exception {
        String post_uri = "/api/procurements/hire";

        String newJson1 = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson1);

        MvcResult post_mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson1)).andExpect(status().is2xxSuccessful())
                .andReturn();

        int status1 = post_mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);

        String response1 = post_mvcResult.getResponse().getContentAsString();
        assertThat(response1.contains("Ahmed"));


        String update_uri = "/api/procurements/hire/{id}";

        String newJson2 = "{\n" +
                "        \"entryId\": \"ID1\",\n" +
                "        \"entryName\": \"New Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Mirlind\",\n" +
                "        \"nameOfConstructionSite\": \"Site 2\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-08-10\",\n" +
                "            \"endDate\": \"2020-08-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson2);

        MvcResult update_mvcResult = mockMvc.perform(put(update_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson2)).andExpect(status().is4xxClientError())
                .andReturn();

        int status2 = update_mvcResult.getResponse().getStatus();
        assertThat(status2).isEqualTo(400);

        String response2 = update_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("not a valid Long value"));

    }

    //    Covers requirement CC3 (Cancel Plant Hire Request if startDate is in the future - should pass)
    @Test
    @WithMockUser(roles="ADMIN")
    public void cancelPlantHireRequest_success() throws Exception {
        String post_uri = "/api/procurements/hire";

        String newJson = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +  //startDate is in the future - Test should pass
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson);

        MvcResult post_mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status1 = post_mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);

        String response = post_mvcResult.getResponse().getContentAsString();
        assertThat(response.contains("PENDING"));

        String delete_uri = "/api/procurements/hire/{id}";

        MvcResult delete_mvcResult = mockMvc.perform(delete(delete_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andReturn();

        int status = delete_mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        String response2 = delete_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("CANCELLED"));
    }

    //    Covers requirement CC3 (Cancel Plant Hire Request if startDate is in the past - should fail)
    @Test
    @WithMockUser(roles="ADMIN")
    public void cancelPlantHireRequest_failure() throws Exception {
        String post_uri = "/api/procurements/hire";

        String newJson = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +  //startDate is in the future - Test should pass
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson);

        MvcResult post_mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status1 = post_mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);

        String response = post_mvcResult.getResponse().getContentAsString();
        assertThat(response.contains("PENDING"));


        MvcResult result = mockMvc.perform(
                delete("/api/procurements/hire/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        String resp = result.getResponse().getContentAsString();
        System.out.println("AAAAAAAAAAAAAAAAAAAAA"+ resp);
        assertThat(resp.contains("2020-04-01"));

        String delete_uri = "/api/procurements/hire/{id}";

        MvcResult delete_mvcResult = mockMvc.perform(delete(delete_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andReturn();

        int status = delete_mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(400);

        String response2 = delete_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("You can't cancel the Hire Request in this stage"));
    }

    //    Covers requirement CC4 (List All Plant Hire Requests)
    @Test
    @WithMockUser(roles="ADMIN")
    public void listAllHireRequests() throws Exception {
        String uri_post = "/api/procurements/hire";

        // create plant hire request 1
        String newJson1 = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test A\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson1);

        // create plant hire request 2
        String newJson2 = "{\n" +
                "        \"entryId\": 2,\n" +
                "        \"entryName\": \"Test B\",\n" +
                "        \"nameOfSiteEngineer\": \"Mirlind\",\n" +
                "        \"nameOfConstructionSite\": \"Site 2\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson2);

        //insert plant hire request 1
        MvcResult mvcResult1 = mockMvc.perform(post(uri_post)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson1))
                .andReturn();

        //insert plant hire request 2
        MvcResult mvcResult2 = mockMvc.perform(post(uri_post)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson2))
                .andReturn();

        // retrieve all plant hire requests
        MvcResult get_result = mockMvc.perform(
                get("/api/procurements/hire"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(get_result.getResponse().getStatus()).isEqualTo(200);

        String response = get_result.getResponse().getContentAsString();
        assertThat(response.contains("Test A"));
        assertThat(response.contains("Test B"));

        //assertThat(plantHireRequestRepository.findAll().size()).isEqualTo(1);
//        JSONArray jsonArray = new JSONArray(get_result.getResponse().getContentAsString());
//
//        for(int i=0;i<jsonArray.length();i++){
//            JSONObject entryResp = jsonArray.getJSONObject(i);
//            assertThat(entryResp.has("entryId")).isTrue();
//            assertThat(entryResp.has("entryId")).isEqualTo(1);
//            assertThat(entryResp.getDouble("entryId")).isNotNull();
//            assertThat(entryResp.getDouble("entryId")).isNotNaN();
//        }

    }

    //    Covers requirement CC4 (List Specific Hire Request)
    @Test
    @WithMockUser(roles="ADMIN")
    public void listSpecificHireRequest() throws Exception {
        String post_uri = "/api/procurements/hire";

        String newJson = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status1 = mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);

        String get_uri = "/api/procurements/hire/{id}";

        MvcResult get_result = mockMvc.perform(
                get(get_uri, 1))
                //.andExpect(status().isOk())
                .andReturn();


        int status2 = get_result.getResponse().getStatus();
        assertThat(status2).isEqualTo(200);

        String response = get_result.getResponse().getContentAsString();
        assertThat(response.contains("Test Entry"));
    }

    //    Covers requirement CC5 (Approve Specific Plant Hire Request)
    @Test
    @WithMockUser(roles="ADMIN")
    public void approveSpecificPlantHireRequest() throws Exception {
        String post_uri = "/api/procurements/hire";

        String newJson = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson);

        // run post method to create the plant hire request
        MvcResult mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        // assert the creation of the plant hire request
        int status1 = mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);

        String newJson2 = "{\n"+
                "        \"comment\": \"First Approval Cycle\",\n" +
                "        }\n";

        String approve_uri = "/api/procurements/hire/{id}/approve";

        MvcResult approve_mvcResult = mockMvc.perform(put(approve_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        int status2 = approve_mvcResult.getResponse().getStatus();
        assertThat(status2).isEqualTo(200);

        String response2 = approve_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("ACCEPTED"));

    }

    //    Covers requirement CC5 (Approve Cancelled Plant Hire Request - should fail)
    @Test
    @WithMockUser(roles="ADMIN")
    public void approveCancelledPlantHireRequest() throws Exception {
        String post_uri = "/api/procurements/hire";

        String newJson = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson);

        MvcResult post_mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        int status1 = post_mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);

        String response = post_mvcResult.getResponse().getContentAsString();
        assertThat(response.contains("PENDING"));

        String delete_uri = "/api/procurements/hire/{id}";

        // Cancel the Hire Request
        MvcResult delete_mvcResult = mockMvc.perform(delete(delete_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andReturn();

        int status = delete_mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        String response2 = delete_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("CANCELLED"));

        String newJson2 = "{\n"+
                "        \"comment\": \"First Approval Cycle\",\n" +
                "        }\n";

        String approve_uri = "/api/procurements/hire/{id}/approve";

        // Approve the canceled hire request (should fail)
        MvcResult approve_mvcResult = mockMvc.perform(put(approve_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andExpect(status().is4xxClientError())
                .andReturn();

        int status_approved = approve_mvcResult.getResponse().getStatus();
        assertThat(status_approved).isEqualTo(400);

        String response_approved = approve_mvcResult.getResponse().getContentAsString();
        assertThat(response_approved.contains("You can't approve the Hire Request in this stage"));
    }



    //    Covers requirement CC5 (Reject Specific Plant Hire Request)
    @Test
    @WithMockUser(roles="ADMIN")
    public void rejectSpecificPlantHireRequest() throws Exception {
        String post_uri = "/api/procurements/hire";

        String newJson = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson);

        // run post method to create the plant hire request
        MvcResult mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        // assert the creation of the plant hire request
        int status1 = mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);

        String newJson2 = "{\n"+
                "        \"comment\": \"First Rejection\",\n" +
                "        }\n";

        String reject_uri = "/api/procurements/hire/{id}/reject";

        MvcResult reject_mvcResult = mockMvc.perform(put(reject_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        int status2 = reject_mvcResult.getResponse().getStatus();
        assertThat(status2).isEqualTo(200);

        String response2 = reject_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("REJECTED"));
    }

    //    Covers requirement CC5 (Reject Cancelled Plant Hire Request - should fail)
    @Test
    @WithMockUser(roles="ADMIN")
    public void rejectCancelledPlantHireRequest() throws Exception {
        String post_uri = "/api/procurements/hire";

        String newJson = "{\n" +
                "        \"entryId\": 1,\n" +
                "        \"entryName\": \"Test Entry\",\n" +
                "        \"nameOfSiteEngineer\": \"Ahmed\",\n" +
                "        \"nameOfConstructionSite\": \"Site 1\",\n" +
                "        \"supplier\": \"RentIT\",\n" +
                "        \"rentalPeriod\": {\n" +
                "            \"startDate\": \"2020-07-10\",\n" +
                "            \"endDate\": \"2020-07-20\"\n" +
                "        }\n" +
                "    }";
        System.out.println(newJson);

        // run post method to create the plant hire request
        MvcResult mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        // assert the creation of the plant hire request
        int status1 = mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);


        String delete_uri = "/api/procurements/hire/{id}";

        // Cancel the Hire Reqest
        MvcResult delete_mvcResult = mockMvc.perform(delete(delete_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andReturn();

        int delete_status = delete_mvcResult.getResponse().getStatus();
        assertThat(delete_status).isEqualTo(200);

        String delete_response = delete_mvcResult.getResponse().getContentAsString();
        assertThat(delete_response.contains("CANCELLED"));


        String newJson2 = "{\n"+
                "        \"comment\": \"First Rejection\",\n" +
                "        }\n";

        String reject_uri = "/api/procurements/hire/{id}/reject";

        // Reject the canceled hire request (should fail)
        MvcResult reject_mvcResult = mockMvc.perform(put(reject_uri, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson2))
                .andExpect(status().is4xxClientError())
                .andReturn();

        int status2 = reject_mvcResult.getResponse().getStatus();
        assertThat(status2).isEqualTo(400);

        String response2 = reject_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("You can't reject the Hire Request in this stage"));
    }

//        Covers requirement CC8
//    @Test
//    @WithMockUser(roles="ADMIN")
//    @Sql(scripts="/plantHire2.sql")
//    public void extendPlantHireRequestWithFailure() throws Exception {
//        String post_uri = "/api/procurements/hire/{id}/extend";
//        Resource responseBody = new ClassPathResource("po.json", this.getClass());
//        PurchaseOrderDTO purchaseOrderDTO =
//                mapper.readValue(responseBody.getFile(), new TypeReference<PurchaseOrderDTO>() { });
//
//        PlantHireRequest plantHireRequest = new PlantHireRequest();
//        PlantHireRequestDTO plantHireRequestDTO = new PlantHireRequestDTO();
//        plantHireRequestDTO.setRentalPeriod(BusinessPeriod.of(null,LocalDate.of(2020, 7, 31)).toDTO());
//        when(plantHiringService.extendPlantHireRequest(1L, plantHireRequestDTO)).thenReturn(purchaseOrderDTO);
//
//        String newJson = "{\n" +
//                "    \"rentalPeriod\": {\n" +
//                "        \"endDate\": \"2020-07-31\"\n" +
//                "    }\n" +
//                "}";
//
//        System.out.println(newJson);
//
//        MvcResult mvcResult = mockMvc.perform(put(post_uri,1)
//                .contentType(MediaType.APPLICATION_JSON)
//                .characterEncoding("UTF-8")
//                .content(newJson))
//                .andReturn();
//
//        String response = mvcResult.getResponse().getContentAsString();
//        System.out.println("XXXXXXXXXXX"+response);
//        // assert the creation of the plant hire request
//        int status = mvcResult.getResponse().getStatus();
//        assertThat(status).isEqualTo(200);
//
//        assertThat(response).contains("ACCEPTED");
//    }
    }