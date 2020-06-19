package com.example.demo.inventory.application.service;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.rest.InventoryRestController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class PlantInventoryEntryAssembler extends RepresentationModelAssemblerSupport<PlantInventoryEntry, PlantInventoryEntryDTO> {

    public PlantInventoryEntryAssembler(){
        super(InventoryRestController.class, PlantInventoryEntryDTO.class);
    }

    @Override
    public PlantInventoryEntryDTO toModel(PlantInventoryEntry plantInventoryEntry) {
        PlantInventoryEntryDTO dto = createModelWithId(plantInventoryEntry.getId(), plantInventoryEntry);
        dto.set_id(plantInventoryEntry.getId());
        dto.setName(plantInventoryEntry.getName());
        dto.setPrice(plantInventoryEntry.getPrice());
        dto.setDescription(plantInventoryEntry.getDescription());

        return dto;
    }
}
