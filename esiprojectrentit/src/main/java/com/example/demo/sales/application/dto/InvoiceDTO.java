package com.example.demo.sales.application.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceDTO extends RepresentationModel<InvoiceDTO> {

    String invoiceNumber;
    String supplier;
    LocalDate invoiceDate;
    BigDecimal total;
    Long poID;
}

