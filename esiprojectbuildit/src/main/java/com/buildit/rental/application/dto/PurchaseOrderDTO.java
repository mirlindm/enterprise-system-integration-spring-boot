package com.buildit.rental.application.dto;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.rental.domain.model.POStatus;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseOrderDTO extends RepresentationModel<PurchaseOrderDTO> {
    private Long id;
    private POStatus status;
    private PlantInventoryEntryDTO plantEntry;
    private BusinessPeriod rentalPeriod;
    private Link _links;
    private BigDecimal total;
    private String customerCompany;
}
