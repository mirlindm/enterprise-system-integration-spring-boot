package com.buildit.payables.application.dto;

import com.buildit.rental.application.dto.PurchaseOrderDTO;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RemittanceDTO extends RepresentationModel<RemittanceDTO> {

    private Long paymentID;
    private String supplier;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private PurchaseOrderDTO po;
    private String bankName;

}
