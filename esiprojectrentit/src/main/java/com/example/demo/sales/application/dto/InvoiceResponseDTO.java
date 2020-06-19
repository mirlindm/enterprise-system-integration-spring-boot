package com.example.demo.sales.application.dto;

import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceResponseDTO extends RepresentationModel<InvoiceResponseDTO> {

    Long _id;
    String invoiceNumber;
    String supplier;
    LocalDate invoiceDate;
    BigDecimal total;
    Long poID;
    List<Link> _links;

}

