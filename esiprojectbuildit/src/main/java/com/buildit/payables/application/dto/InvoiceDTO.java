package com.buildit.payables.application.dto;

import com.buildit.payables.domain.model.InvStatus;
import com.buildit.payables.domain.model.Invoice;
import com.buildit.rental.domain.model.PurchaseOrder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceDTO extends RepresentationModel<InvoiceDTO> {

    private Long _id;
    private String invoiceNumber;
    private String supplier;
    private LocalDate invoiceDate;
    private BigDecimal total;
    private Long poID;
    private InvStatus status;

    public Invoice toInvoice(PurchaseOrder po, InvoiceDTO invoiceDTO, InvStatus status){
        return Invoice.of(po, invoiceDTO, status);
    }
}
