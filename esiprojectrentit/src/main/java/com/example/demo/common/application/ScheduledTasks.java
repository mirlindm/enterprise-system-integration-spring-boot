package com.example.demo.common.application;

import com.example.demo.common.application.MailHelper;
import com.example.demo.sales.domain.model.Invoice;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.InvoiceRepository;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.swing.plaf.PanelUI;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTasks {
    private static InvoiceRepository invoiceRepository;
    private static PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    public ScheduledTasks(InvoiceRepository invoiceRepo, PurchaseOrderRepository poRepo){
        invoiceRepository = invoiceRepo;
        purchaseOrderRepository = poRepo;
    }

    // Requirement PS14
    // Running every 10 second
    @Scheduled(fixedRate = 10*1000)
    public static List<Invoice> remindUnpaidInvoices() throws IOException, MessagingException {
        // System.out.println("Remind unpaid invoices");
        List<Invoice> invoices = invoiceRepository.findAll();
        List<Invoice> remindedInvoices = new ArrayList<>();

        for(Invoice invoice : invoices){
            LocalDate dueDate = invoice.getDueDate();

            if(!invoice.getIsPaid()
                    && (
                            LocalDate.now().plusDays(2).isEqual(dueDate)
                            || LocalDate.now().plusDays(5).isEqual(dueDate)
                            || LocalDate.now().plusDays(10).isEqual(dueDate)
                    )
            ){
                if(invoice.getPurchaseOrder() != null){
                    MailHelper.sendmail("Payment is not received for your purchase order",
                            invoice.getPurchaseOrder().getContactEmail()
                    );
                }

                remindedInvoices.add(invoice);
            }
        }

        return remindedInvoices;
    }

//    private static void populateDb(){
//        List<PurchaseOrder> pos = purchaseOrderRepository.findAll();
//
//        for(PurchaseOrder po : pos){
//            if(po.getInvoice() == null){
//                System.out.println("Adding invoice to po");
//                Invoice invoice = new Invoice();
//                invoice.setDueDate(LocalDate.now().plusDays(2));
//                invoice.setPurchaseOrder(po);
//                invoiceRepository.save(invoice);
//
//                po.setInvoice(invoice);
//                purchaseOrderRepository.save(po);
//            }else{
//                System.out.println("no invioce added");
//            }
//        }
//    }
}
