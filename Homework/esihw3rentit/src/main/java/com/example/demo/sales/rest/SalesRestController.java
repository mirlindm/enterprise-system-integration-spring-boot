package com.example.demo.sales.rest;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.application.services.SalesService;
import com.example.demo.sales.domain.model.POExtensionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/sales")
public class SalesRestController {
    @Autowired
    InventoryService inventoryService;
    @Autowired
    SalesService salesService;

    @GetMapping("/plants")
    public CollectionModel<PlantInventoryEntryDTO> findAvailablePlants(
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
        headers.setLocation(newlyCreatePODTO.getRequiredLink(IanaLinkRelations.SELF).toUri());

        return new ResponseEntity<>(newlyCreatePODTO, headers, HttpStatus.CREATED);
    }

    @PostMapping("/orders/{id}/accept")
    public PurchaseOrderDTO acceptPurchaseOrder(@PathVariable Long id) throws Exception {
        try {
            return salesService.acceptPO(id);
        } catch(Exception ex) {
            // Add code to Handle Exception (Change return null with the solution)
            return null;
        }
    }

    @DeleteMapping("/{id}/reject")
    public PurchaseOrderDTO rejectPurchaseOrder(@PathVariable Long id) throws Exception {
        try {
            return salesService.rejectPO(id);
        } catch(Exception ex) {
            // Add code to Handle Exception (Change return null with the solution)
            return null;
        }
    }

    @GetMapping("/orders/{id}/extensions")
    public CollectionModel<EntityModel<POExtensionDTO>> retrievePurchaseOrderExtensions(@PathVariable("id") Long id) {
        List<EntityModel<POExtensionDTO>> result = new ArrayList<>();
        POExtensionDTO extension = new POExtensionDTO();
        extension.setEndDate(LocalDate.now().plusWeeks(1));

        result.add(new EntityModel<>(extension));
        return new CollectionModel<>(result,
                linkTo(methodOn(SalesRestController.class).retrievePurchaseOrderExtensions(id))
                        .withSelfRel()
                        .andAffordance(afford(methodOn(SalesRestController.class).requestPurchaseOrderExtension(null, id))));
    }

    @PostMapping("/orders/{id}/extensions")
    public EntityModel<?> requestPurchaseOrderExtension(@RequestBody POExtensionDTO extension, @PathVariable("id") Long id) {
        // Add code to handle the extension of the purchase order
        return null;
    }


}