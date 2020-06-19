package com.buildit.maintenance.application.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
public class PlantInventoryItemDTO extends RepresentationModel<PlantInventoryItemDTO> {
    private Long _id;
    private String serialNumber;
    private String equipmentCondition;
}
