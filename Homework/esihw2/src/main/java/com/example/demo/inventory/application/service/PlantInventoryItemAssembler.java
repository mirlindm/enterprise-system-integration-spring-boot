package com.example.demo.inventory.application.service;

import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.rest.InventoryRestController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class PlantInventoryItemAssembler extends ResourceAssemblerSupport<PlantInventoryItem, PlantInventoryItemDTO> {
    public PlantInventoryItemAssembler(){
        super(InventoryRestController.class, PlantInventoryItemDTO.class);
    }

    @Override
    public PlantInventoryItemDTO toResource(PlantInventoryItem plantInventoryItem) {
        PlantInventoryItemDTO dto = createResourceWithId(plantInventoryItem.getId(), plantInventoryItem);
        dto.set_id(plantInventoryItem.getId());
        dto.setEquipmentCondition(plantInventoryItem.getEquipmentCondition());
        dto.setPlantInfo(plantInventoryItem.getPlantInfo());
        dto.setSerialNumber(plantInventoryItem.getSerialNumber());

        return dto;
    }
}
