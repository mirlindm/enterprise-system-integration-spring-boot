package com.buildit.procurement.rest.controller;

import com.buildit.Auth.application.service.TokenProvider;
import com.buildit.BuilditApplication;
import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.maintenance.domain.repositories.PlantInventoryItemRepository;
import com.buildit.payables.application.services.PayablesService;
import com.buildit.payables.domain.repositories.InvoiceRepository;
import com.buildit.payables.domain.repositories.PaymentRepository;
import com.buildit.payables.rest.controller.PaymentPayablesRestControllerTests;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.application.service.PlantHiringService;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.domain.repositories.PurchaseOrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {BuilditApplication.class,
        ProcurementRestControllerMockTests.ProcurementServiceMock.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
@WebAppConfiguration
@Sql(scripts="/plantHire.sql")
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //added
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) //added
public class ProcurementRestControllerMockTests {
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
    PaymentRepository paymentRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    PayablesService payablesService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Configuration
    static class ProcurementServiceMock {
        @Bean
        public PlantHiringService plantHiringService () { return Mockito.mock(PlantHiringService.class); }
    }

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
//        //when(rentalService.findAvailablePlants("exc", startDate, endDate)).thenReturn(list);
//        when(rentalService.findAvailablePlants("exc",startDate, endDate)).thenReturn(list);
//
//        String accessToken = tokenProvider.generateToken(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user1","password1")));
//        System.out.println(accessToken);
//        MvcResult result = mockMvc.perform(
//                get("/api/procurements/plants?name={name}&startDate={start}&endDate={end}", "exc", startDate, endDate)
//                        .header("Authorization", "Bearer " + accessToken)
//        )
//                //.andExpect()
//                .andReturn();
//
//        // Add test expectations
//    }


    //Covers requirement CC8
    @Test
    @WithMockUser(roles="ADMIN")
    @Sql(scripts="/plantHire2.sql")
    public void extendPlantHireRequestWithFailure() throws Exception {
        String post_uri = "/api/procurements/hire/{id}/extend";
        Resource responseBody = new ClassPathResource("po.json", this.getClass());
        PurchaseOrderDTO purchaseOrderDTO =
                mapper.readValue(responseBody.getFile(), new TypeReference<PurchaseOrderDTO>() { });

        PlantHireRequest plantHireRequest = new PlantHireRequest();
        PlantHireRequestDTO plantHireRequestDTO = new PlantHireRequestDTO();
        plantHireRequestDTO.setRentalPeriod(BusinessPeriod.of(null,LocalDate.of(2020, 7, 31)).toDTO());
        when(plantHiringService.extendPlantHireRequest(1L, plantHireRequestDTO)).thenReturn(purchaseOrderDTO);

        String newJson = "{\n" +
                "    \"rentalPeriod\": {\n" +
                "        \"endDate\": \"2020-07-31\"\n" +
                "    }\n" +
                "}";

        System.out.println(newJson);

        MvcResult mvcResult = mockMvc.perform(put(post_uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        System.out.println("XXXXXXXXXXX"+response);
        // assert the creation of the plant hire request
        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        assertThat(response).contains("ACCEPTED");
    }


}
