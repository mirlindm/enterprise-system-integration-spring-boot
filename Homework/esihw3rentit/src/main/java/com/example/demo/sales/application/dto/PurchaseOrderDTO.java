package com.example.demo.sales.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.sales.domain.model.POStatus;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;


@Data
public class PurchaseOrderDTO extends RepresentationModel<PurchaseOrderDTO> {
    Long _id;
    BusinessPeriodDTO rentalPeriod;
    PlantInventoryEntryDTO plant;
    POStatus status;
}
