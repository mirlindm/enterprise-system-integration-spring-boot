package com.example.demo.inventory.application.service;


import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.rest.InventoryRestController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class PlantInventoryItemAssembler  extends RepresentationModelAssemblerSupport<PlantInventoryItem, PlantInventoryItemDTO> {

    public PlantInventoryItemAssembler(){
        super(InventoryRestController.class, PlantInventoryItemDTO.class);
    }

    @Override
    public PlantInventoryItemDTO toModel(PlantInventoryItem plantInventoryItem) {
        PlantInventoryItemDTO dto = createModelWithId(plantInventoryItem.getId(), plantInventoryItem);
        dto.set_id(plantInventoryItem.getId());
        dto.setSerialNumber(plantInventoryItem.getSerialNumber());
        dto.setEquipmentCondition(plantInventoryItem.getEquipmentCondition());

        return dto;
    }

}
