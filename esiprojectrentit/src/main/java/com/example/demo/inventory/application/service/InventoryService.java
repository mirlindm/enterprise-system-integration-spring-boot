package com.example.demo.inventory.application.service;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.domain.model.*;
import com.example.demo.inventory.domain.repository.*;
import com.example.demo.maintenance.domain.repository.MaintenanceTaskRepository;
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
    MaintenanceTaskRepository maintenanceTaskRepository;

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


    //Requirement PS1
    public List<PlantInventoryEntryDTO> getAllAvailablePlantEntries(String startDate, String endDate){
//        System.out.println("getAllAvailablePlants");
        BusinessPeriod requestedPeriod = BusinessPeriod.of(LocalDate.parse(startDate), LocalDate.parse(endDate));
//        System.out.println("Request period: "+requestedPeriod);

        ArrayList<PlantInventoryEntryDTO> response = new ArrayList<>();
        List<PlantReservation> allReservations = reservationRepository.findAll();

        for(PlantInventoryItem item: itemRepository.findAllServiceable()){
            Boolean isAvailable = true;
//            System.out.println("------------------------------------------------");
//            System.out.println("item id / entry id: "+item.getId() + " / "+item.getPlantInfo().getId());

            for(PlantReservation reservation: allReservations){
                if(item.getId() == reservation.getPlant().getId() &&
                        reservation.getSchedule().overlapsWith(requestedPeriod)){
                    isAvailable = false;
                    break;
//                    System.out.println(reservation.getId()+" overlaps / "+reservation.getSchedule());
//                    System.out.println("plant entry id: "+item.getPlantInfo().getId());
                }
            }

            PlantInventoryEntryDTO assembledEntry = entryAssembler.toModel(item.getPlantInfo(),requestedPeriod);
            if(isAvailable && !response.contains(assembledEntry)){
                response.add(assembledEntry);
            }
        }

        return response;
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

    //Requirement PS2
    public PlantInventoryEntryDTO getPlantEntry(Long id){
        PlantInventoryEntry entry = entryRepository.findById(id).orElse(null);
        return entryAssembler.toModel(entry);
    }

    //Requirement PS3
    public String checkAvailabilityOfEntry(Long id, String startDate, String endDate){
        BusinessPeriod requestedPeriod = BusinessPeriod.of(LocalDate.parse(startDate), LocalDate.parse(endDate));
        PlantInventoryEntry entry = entryRepository.findById(id).orElse(null);

        List<PlantInventoryItem> items = itemRepository.findAllServiceableByEntryId(entry.getId());
        List<PlantReservation> reservations = reservationRepository.findAll();
//        System.out.println("items: "+items);
//        System.out.println("reservations: "+reservations);

        for(PlantInventoryItem item: items){
            Boolean isAvailable = true;
//            System.out.println("--------------------");
//            System.out.println("item / plant: "+item.getId() + " / "+item.getPlantInfo().getId());

            for(PlantReservation reservation: reservations){
                if(reservation.getPlant().getId() == item.getId()
                        && reservation.getSchedule().overlapsWith(requestedPeriod)){
//                    System.out.println("OVERLAPS " + reservation);
                    isAvailable = false;
                    break;
                }
            }

            if(isAvailable){
                return "{\"available\": true}";
            }
        }

        return "{\"available\": false}";
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
