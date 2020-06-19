package com.example.demo;

import com.example.demo.common.application.ScheduledTasks;
import com.example.demo.sales.domain.model.Invoice;
import com.example.demo.sales.domain.repository.InvoiceRepository;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class) // Check if the name of this class is correct or not
@WebAppConfiguration
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ScheduledTaskTests {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

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
