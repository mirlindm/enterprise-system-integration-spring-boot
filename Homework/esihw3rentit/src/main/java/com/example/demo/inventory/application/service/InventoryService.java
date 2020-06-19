package com.example.demo.inventory.application.service;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.*;
import com.example.demo.inventory.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    PlantInventoryEntryRepository entryRepository;

    @Autowired
    PlantInventoryItemRepository itemRepository;


    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    @Autowired
    PlantInventoryItemAssembler itemAssembler;

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    @Autowired
    PlantInventoryItemAssembler plantInventoryItemAssembler;

    @Autowired
    PlantReservationRepository reservationRepository;

    @Autowired
    PlantInventoryEntryAssembler entryAssembler;

    public PlantInventoryEntry findEntryById(Long id) {
        return plantInventoryEntryRepository.findById(id).orElse(null);
    }

    public PlantInventoryItem findPlantById(Long id) {
        return plantInventoryItemRepository.findById(id).orElse(null);
    }

    public PlantInventoryItem findItemById(Long id) {
        return plantInventoryItemRepository.findById(id).orElse(null);
    }

    public CollectionModel<PlantInventoryEntryDTO> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate)     {
        List<PlantInventoryEntry> entries = inventoryRepository.findAvailablePlants(name, startDate, endDate);
        return plantInventoryEntryAssembler.toCollectionModel(entries);
    }

    public CollectionModel<PlantInventoryItemDTO> findAvailableItems(String name, LocalDate startDate, LocalDate endDate) {
        List<PlantInventoryItem> items = inventoryRepository.findAvailableItems(name, startDate, endDate);
        return plantInventoryItemAssembler.toCollectionModel(items);
    }

    public List<PlantInventoryItem> findAvailableItemsE(String name, LocalDate startDate, LocalDate endDate) {
        return inventoryRepository.findAvailableItems(name, startDate, endDate);
    }


    public List<PlantInventoryEntryDTO> getAllPlantEntries(){
        System.out.println("getAllPlants");

        ArrayList<PlantInventoryEntryDTO> response = new ArrayList<>();
        List<PlantInventoryEntry> allEntries = entryRepository.findAll();
//        System.out.println(allEntries);

        for(PlantInventoryEntry entry: allEntries){
            response.add(entryAssembler.toModel(entry));
        }

        return response;
    }

    public PlantInventoryEntryDTO getPlantEntry(Long id){
        PlantInventoryEntry entry = entryRepository.findById(id).orElse(null);
        return entryAssembler.toModel(entry);
    }

    public List<PlantInventoryItemDTO> getPlantItems(Long id){
        PlantInventoryEntry entry = entryRepository.findById(id).orElse(null);
        List<PlantInventoryItem> items = itemRepository.findAllByPlantInfo(entry);

        List<PlantInventoryItemDTO> response = new ArrayList<>();
        for(PlantInventoryItem item: items){
            response.add(itemAssembler.toModel(item));
        }

        return response;
    }

    public PlantInventoryItemDTO getPlantItem(Long id){
        PlantInventoryItem plant = itemRepository.findById(id).orElse(null);
        return itemAssembler.toModel(plant);
    }

    public PlantInventoryItemDTO updatePlantItemStatus(Long id, String newstatus) {
        PlantInventoryItem item = itemRepository.findById(id).orElse(null);
        item.setEquipmentCondition(EquipmentCondition.valueOf(newstatus));
        itemRepository.save(item);
        PlantInventoryItem item2 = itemRepository.findById(id).orElse(null);
        return itemAssembler.toModel(item2);
    }

    public PlantInventoryItemDTO updatePlantItemStatus_v2(Long id, PlantInventoryItemDTO item) {
        PlantInventoryItem item_db = itemRepository.findById(id).orElse(null);
        item_db.setEquipmentCondition(item.getEquipmentCondition());
        itemRepository.save(item_db);
        return itemAssembler.toModel(item_db);
    }


    public PlantInventoryItemDTO getPlantInvItemById(Long id){
        PlantInventoryItem item = itemRepository.findById(id).orElse(null);
        return itemAssembler.toModel(item);
    }




}
