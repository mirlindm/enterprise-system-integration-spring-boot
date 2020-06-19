package com.example.demo.inventory.application.dto;

import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import java.math.BigDecimal;

@Data
public class PlantInventoryItemDTO extends ResourceSupport {
    Long _id;
    String serialNumber;
    EquipmentCondition equipmentCondition;
    PlantInventoryEntry plantInfo;
}
