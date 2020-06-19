package com.buildit.payables.rest.controller;

import com.buildit.Auth.application.service.TokenProvider;
import com.buildit.BuilditApplication;
import com.buildit.maintenance.domain.repositories.PlantInventoryItemRepository;
import com.buildit.payables.application.dto.PaymentDTO;
import com.buildit.payables.application.services.PayablesService;
import com.buildit.payables.domain.repositories.InvoiceRepository;
import com.buildit.payables.domain.repositories.PaymentRepository;
import com.buildit.procurement.application.service.PlantHiringService;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.domain.model.PurchaseOrder;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {BuilditApplication.class,
        PaymentPayablesRestControllerTests.PayablesServiceMock.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
@WebAppConfiguration
//@Sql(scripts="/plantHire.sql")
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //added
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) //added
public class PaymentPayablesRestControllerTests {
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
    static class PayablesServiceMock {
        @Bean
        public PayablesService payablesService() { return Mockito.mock(PayablesService.class); }
    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
    }

    //    Covers requirement CC12 (Submit Remittance Advice from BuildIT)
    @Test
    @WithMockUser(roles="ADMIN")
    public void submitRemittanceAdvice_success() throws Exception {
        String post_uri = "/api/procurements/hire";

        String post_newJson = "{\n" +
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
        System.out.println(post_newJson);

        // run post method to create the plant hire request
        MvcResult post_mvcResult = mockMvc.perform(post(post_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(post_newJson))
                .andReturn();

        // assert the creation of the plant hire request
        int post_status = post_mvcResult.getResponse().getStatus();
        assertThat(post_status).isEqualTo(201);

        String newJson2 = "{\n" +
                "\t\"comment\": \"Second Approval cycle\"\n" +
                "}";

        String approve_uri = "/api/procurements/hire/{id}/approve";

        MvcResult approve_mvcResult = mockMvc.perform(put(approve_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(newJson2))
                .andExpect(status().is2xxSuccessful()) //?
                .andReturn();

        int approve_status = approve_mvcResult.getResponse().getStatus();
        assertThat(approve_status).isEqualTo(200);

        String approve_response = approve_mvcResult.getResponse().getContentAsString();
        assertThat(approve_response.contains("ACCEPTED"));


        // Invoice Creation API
        String invoice_uri = "/api/payables/invoices";

        PurchaseOrder po = purchaseOrderRepository.findAll().get(0);

        String invoice_newJson = "{\n" +
                "\t\"invoiceNumber\": \"Invoice 1\",\n" +
                "\t\"supplier\": \"RentIT\",\n" +
                "\t\"invoiceDate\": \"2020-05-11\",\n" +
                "\t\"total\": 1000,\n" +
                "\t\"poID\": " + po.getReferenceID() + "\n" +
                "}";
        System.out.println(invoice_newJson);

        // run post method to create the invoice for the given plant hire
        MvcResult invoice_mvcResult = mockMvc.perform(post(invoice_uri)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(invoice_newJson))
                .andReturn();

        // assert the creation of the invoice
        int invoice_status = invoice_mvcResult.getResponse().getStatus();
        assertThat(invoice_status).isEqualTo(201);

        String invoice_response = invoice_mvcResult.getResponse().getContentAsString();
        assertThat(invoice_response.contains("Invoice 1"));

        // Approve Invoice API
        String approveInvoice_uri = "/api/payables/invoices/{id}";

        // Run the PUT request to the URI to approve the invoice
        MvcResult approve_invoice_mvcResult = mockMvc.perform(put(approveInvoice_uri, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // assert the first approval of the invoice
        int approve_invoice_status = approve_invoice_mvcResult.getResponse().getStatus();
        assertThat(approve_invoice_status).isEqualTo(201);

        String approve_invoice_response = approve_invoice_mvcResult.getResponse().getContentAsString();
        assertThat(approve_invoice_response.contains("APPROVED"));

        String remittance_uri = "/api/payables/invoices/1/pay";

        String remittance_newJson = "{\n" +
                "\t\"paymentDate\": \"2020-05-13\",\n" +
                "\t\"bankName\": \"Swedbank\",\n" +
                "\t\"amountPaid\": 1000\n" +
                "}";
        System.out.println(invoice_newJson);

        Resource responseBody = new ClassPathResource("payment.json", this.getClass());
        PaymentDTO paymentDTO =
                mapper.readValue(responseBody.getFile(), new TypeReference<PaymentDTO>() { });
        when(payablesService.createPayment(1L, paymentDTO)).thenReturn(paymentDTO);

        String accessToken = tokenProvider.generateToken(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user1","password1")));

        // run post method to create the remittance for the given plant hire
        MvcResult remittance_mvcResult = mockMvc.perform(post(remittance_uri)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(remittance_newJson))
                .andReturn();

        // assert the creation of the invoice
        int remittance_status = remittance_mvcResult.getResponse().getStatus();
        assertThat(remittance_status).isEqualTo(201);

        String remittance_response = remittance_mvcResult.getResponse().getContentAsString();
        assertThat(remittance_response.contains("Swedbank"));

    }


}
