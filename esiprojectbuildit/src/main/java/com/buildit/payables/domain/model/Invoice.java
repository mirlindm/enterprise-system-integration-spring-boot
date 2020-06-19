package com.buildit.payables.domain.model;

import com.buildit.payables.application.dto.InvoiceDTO;
import com.buildit.rental.domain.model.PurchaseOrder;
import lombok.Data;


import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    private String supplier;

    private LocalDate invoiceDate;
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private InvStatus status;

    @OneToOne
    private PurchaseOrder po;

    public static Invoice of(PurchaseOrder po, InvoiceDTO invoiceDTO, InvStatus status){
        Invoice invoice = new Invoice();
        invoice.status = status;
        invoice.total = invoiceDTO.getTotal();
        invoice.invoiceDate = LocalDate.now();
        invoice.supplier = invoiceDTO.getSupplier();
        invoice.invoiceNumber = invoiceDTO.getInvoiceNumber();
        invoice.po = po;
        return invoice;
    }

}
