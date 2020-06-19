package com.example.demo.sales.rest;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.application.services.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesRestController {
    @Autowired
    InventoryService inventoryService;
    @Autowired
    SalesService salesService;

    @GetMapping("/plants")
    public List<PlantInventoryEntryDTO> findAvailablePlants(
            @RequestParam(name = "name") String plantName,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
       return inventoryService.findAvailablePlants(plantName,startDate,endDate);
    }

    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") Long id) {
        return salesService.findPO(id);
    }

    @PostMapping("/orders")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO partialPODTO) throws Exception {
        PurchaseOrderDTO newlyCreatePODTO = salesService.createPO(partialPODTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(newlyCreatePODTO.getId().getHref()));
        // The above line won't working until you update PurchaseOrderDTO to extend ResourceSupport

        return new ResponseEntity<PurchaseOrderDTO>(newlyCreatePODTO, headers, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/accept")
    public PurchaseOrderDTO acceptPurchaseOrder(@PathVariable String id) throws Exception {
        try {
            return salesService.acceptPO(id);
        } catch(Exception ex) {
            return null;
        }
    }

    @DeleteMapping("/{id}/accept")
    public PurchaseOrderDTO rejectPurchaseOrder(@PathVariable String id) throws Exception {
        try {
            return salesService.rejectPO(id);
        } catch(Exception ex) {

            return null;
        }

    }
}