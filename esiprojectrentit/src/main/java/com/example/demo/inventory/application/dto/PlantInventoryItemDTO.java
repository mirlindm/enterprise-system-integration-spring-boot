package com.example.demo.inventory.application.dto;

import com.example.demo.inventory.domain.model.EquipmentCondition;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class PlantInventoryItemDTO extends RepresentationModel<PlantInventoryItemDTO> {
    Long _id;
    String serialNumber;
    EquipmentCondition equipmentCondition;
}
