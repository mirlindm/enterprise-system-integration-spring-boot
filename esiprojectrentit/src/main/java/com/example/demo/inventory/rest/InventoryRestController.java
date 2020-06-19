package com.example.demo.inventory.rest;

import com.example.demo.maintenance.application.dto.MaintenanceTaskDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.maintenance.application.service.MaintenanceService;
import com.example.demo.maintenance.domain.model.MaintenanceTaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class InventoryRestController {
    @Autowired
    InventoryService inventoryService;

    @GetMapping("/all")
    public List<PlantInventoryEntryDTO> getAllPlants() {
        return inventoryService.getAllPlantEntries();
    }


    //Requirement PS1
    @GetMapping("/available")
    public List<PlantInventoryEntryDTO> getAllAvailablePlants(@RequestParam String startDate,@RequestParam String endDate) {
        System.out.println("===> get available plants");
        return inventoryService.getAllAvailablePlantEntries(startDate, endDate);
    }

    //Requirement PS2
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlantInventoryEntryDTO fetchPlantEntry(@PathVariable("id") Long id) {
        return inventoryService.getPlantEntry(id);
    }

    //Requirement PS3
    @GetMapping("/{id}/availability")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> checkAvailabilityOfEntry(@PathVariable("id") Long id, @RequestParam String startDate, @RequestParam String endDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        String responseBody = inventoryService.checkAvailabilityOfEntry(id, startDate, endDate);
        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/items")
    @ResponseStatus(HttpStatus.OK)
    public List<PlantInventoryItemDTO> fetchPlantItems(@PathVariable("id") Long id) {
        return inventoryService.getPlantItems(id);
    }

    @GetMapping("/items/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlantInventoryItemDTO fetchPlantInvItemById(@PathVariable("id") Long id) {
        return inventoryService.getPlantInvItemById(id);
    }


//    @PutMapping(value = "/items/{id}")
//    public PlantInventoryItemDTO updatePlantItemStatus(@PathVariable("id") Long id, @RequestBody Map<String, Object> status) throws Exception {
//        try {
//            return inventoryService.updatePlantItemStatus(id, status.get("newstatus").toString());
//        } catch(Exception ex) {
//            return null;
//        }
//    }

    @PutMapping(value = "/items/{id}")
    public PlantInventoryItemDTO updatePlantItemStatus_v2(@PathVariable("id") Long id, @RequestBody PlantInventoryItemDTO item) throws Exception {
        try {
            return inventoryService.updatePlantItemStatus_v2(id, item);
        } catch(Exception ex) {
            return null;
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        System.out.println("ERROR BEING HANDLED: "+ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.BAD_REQUEST);
    }

}

