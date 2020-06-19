package com.example.demo.inventory.rest;

import com.example.demo.inventory.application.dto.MaintenanceTaskDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.inventory.domain.model.MaintenanceTask;
import com.example.demo.inventory.domain.model.MaintenanceTaskRequest;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plants")
public class InventoryRestController {
    @Autowired
    InventoryService inventoryService;

    @GetMapping("/all")
    public List<PlantInventoryEntryDTO> getAllPlants() {
        return inventoryService.getAllPlantEntries();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlantInventoryEntryDTO fetchPlantEntry(@PathVariable("id") Long id) {
        return inventoryService.getPlantEntry(id);
    }

    @GetMapping("/{id}/items")
    @ResponseStatus(HttpStatus.OK)
    public List<PlantInventoryItemDTO> fetchPlantItems(@PathVariable("id") Long id) {
        return inventoryService.getPlantItems(id);
    }

    @GetMapping("/{id}/items/maintenance_tasks/performed")
    @ResponseStatus(HttpStatus.OK)
    public List<MaintenanceTaskDTO> fetchPerformedMaintenanceTasks(@PathVariable("id") Long id) {
        return inventoryService.getMaintenanceTasks(id, true);
    }

    @GetMapping("/{id}/items/maintenance_tasks/scheduled")
    @ResponseStatus(HttpStatus.OK)
    public List<MaintenanceTaskDTO> fetchScheduledMaintenanceTasks(@PathVariable("id") Long id) {
        return inventoryService.getMaintenanceTasks(id, false);
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

    @PostMapping("/items/{id}/maintenance_tasks/create")
    public ResponseEntity<MaintenanceTaskDTO> createMaintenanceTask(@PathVariable("id") Long id, @RequestBody MaintenanceTaskRequest maintenanceTaskRequest) throws Exception{
        System.out.println("In Controller");
        MaintenanceTaskDTO createdDTO = inventoryService.createMaintenanceTask(id, maintenanceTaskRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(createdDTO.getId().getHref()));
        // The above line won't working until you update MaintenanceTaskDTO to extend ResourceSupport

        return new ResponseEntity<MaintenanceTaskDTO>(createdDTO, headers, HttpStatus.CREATED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        System.out.println("ERROR BEING HANDLED: "+ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.BAD_REQUEST);
    }

}
