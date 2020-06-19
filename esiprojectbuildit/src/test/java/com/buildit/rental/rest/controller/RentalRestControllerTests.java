package com.buildit.rental.rest.controller;


import com.buildit.Auth.application.service.TokenProvider;
import com.buildit.BuilditApplication;
import com.buildit.maintenance.domain.repositories.PlantInventoryItemRepository;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.procurement.rest.controller.ProcurementRestControllerTests;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.domain.repositories.PurchaseOrderRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {BuilditApplication.class})
@WebAppConfiguration
//@Sql(scripts="/plantHire.sql");
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //added
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) //added
public class RentalRestControllerTests {
    protected MockMvc mvc; //added

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired //@Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @Autowired
    RentalService rentalService;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    @Autowired
    PlantHireRequestRepository plantHireRequestRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

//    @Configuration
//    static class RentalServiceMock {
////        @Bean
////        public RentalService rentalService() {
////            return Mockito.mock(RentalService.class);
////        }
//    }

//    @Before
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
    }

    //    Covers requirement CC6 (Create PO for approved Hire Requests)
    @Test
    @WithMockUser(roles="ADMIN")
    public void createPOForApprovedHireRequest() throws Exception {
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

        String newJson2 = "{\n" +
                "\t\"comment\": \"Second Approval cycle\"\n" +
                "}";

        String approve_uri = "/api/procurements/hire/{id}/approve";

        // approve the created hire request
        MvcResult approve_mvcResult = mockMvc.perform(put(approve_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson2))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        int status2 = approve_mvcResult.getResponse().getStatus();
        assertThat(status2).isEqualTo(200);

        String response2 = approve_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("ACCEPTED"));

        //Purchase Order automatically created after the Hire Request gets Approved
    }

    //    Covers requirement CC6 (Create PO for rejected Hire Requests )
    @Test
    @WithMockUser(roles="ADMIN")
    public void createPOForRejectedHireRequest() throws Exception {
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

        String newJson2 = "{\n" +
                "\t\"comment\": \"Second Approval cycle\"\n" +
                "}";

        String reject_uri = "/api/procurements/hire/{id}/reject";

        // reject the created hire request
        MvcResult reject_mvcResult = mockMvc.perform(put(reject_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson2))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        int status2 = reject_mvcResult.getResponse().getStatus();
        assertThat(status2).isEqualTo(200);

        String response2 = reject_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("REJECTED"));

//        assertThat(myList.size()).isEqualTo(1);
        PlantHireRequest phr = plantHireRequestRepository.findById(1L).orElse(null);
        assertThat(phr.getPo()).isNull();
        assertThat(phr.getStatus().toString()).isEqualTo("REJECTED");
    }

    //    Covers requirement CC7 (View Submitted POs and their statuses)
    @Test
    @WithMockUser(roles="ADMIN")
    public void listPOsAndStatuses() throws Exception {
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
        //System.out.println(newJson);

        // run post method to create the plant hire request
        MvcResult mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        // assert the creation of the plant hire request
        int status1 = mvcResult.getResponse().getStatus();
        assertThat(status1).isEqualTo(201);

        String newJson2 = "{\n" +
                "\t\"comment\": \"First Approval cycle\"\n" +
                "}";

        String approve_uri = "/api/procurements/hire/{id}/approve";

        // approve the created hire request
        MvcResult approve_mvcResult = mockMvc.perform(put(approve_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson2))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        int status2 = approve_mvcResult.getResponse().getStatus();
        assertThat(status2).isEqualTo(200);

        String response2 = approve_mvcResult.getResponse().getContentAsString();
        assertThat(response2.contains("ACCEPTED"));
        assertThat(response2.contains("First Approval Cycle"));
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXX"+ response2);

        // Fetch All POs API
        String uri = "/api/rental/po";
        //String href = purchaseOrderRepository.findAll().get(0).get_xlink().getHref();
        //String po_id = href.substring(href.lastIndexOf('/') + 1);
        MvcResult mvcResult2 = mockMvc.perform(get(uri)).andExpect(status().is2xxSuccessful()).andReturn();
//        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+purchaseOrderRepository.findAll());
        System.out.println("POOOOOOOOOOOO");
        System.out.println(mvcResult2.getResponse().getContentAsString());
//        System.out.println(plantHireRequestRepository.findAll());
        String res = mvcResult2.getResponse().getContentAsString();
//        List<String> myList = new ArrayList<String>(Arrays.asList(res.split(",")));
//        assertThat(myList.size()).isEqualTo(1);
        PlantHireRequest phr = plantHireRequestRepository.findById(1L).orElse(null);
        assertThat(phr.getPo().getId()).isNotNull();
        assertThat(phr.getStatus().toString()).isEqualTo("ACCEPTED");
    }

}
