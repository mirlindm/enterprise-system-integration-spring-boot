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




    //Requirement PS4
    @PostMapping("/po")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO purchaseOrderDTO) throws Exception {
        PurchaseOrderDTO createPODto = salesService.createPO(purchaseOrderDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(createPODto.getRequiredLink(IanaLinkRelations.SELF).toUri());

        return new ResponseEntity<>(createPODto, headers, HttpStatus.CREATED);
    }

    //Requirement PS4
    @PatchMapping("/po/{id}")
    public PurchaseOrderDTO acceptPurchaseOrder(@PathVariable Long id) throws Exception {
            return salesService.acceptPO(id);
    }

    //Requirement PS4
    @DeleteMapping("/po/{id}")
    public PurchaseOrderDTO rejectPurchaseOrder(@PathVariable Long id) throws Exception {
            return salesService.rejectPO(id);

    }

    //Requirement PS5
    @GetMapping("/po/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO fetchPurchaseOrder(@PathVariable("id") Long id) {
        return salesService.findPO(id);
    }

    //Requirement PS6
    @PutMapping("/po/{id}/extend")
    public ResponseEntity<PurchaseOrderDTO> requestPOExtension(@RequestBody PurchaseOrderDTO eDto, @PathVariable("id") Long id) throws  Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        PurchaseOrderDTO extendPO = salesService.extendPO(eDto,id);
        headers.setLocation(extendPO.getRequiredLink(IanaLinkRelations.SELF).toUri());
            return new ResponseEntity<>(extendPO, headers, HttpStatus.OK);
    }


    //Requirement PS7
    @DeleteMapping("/po/{id}/cancel")
    public PurchaseOrderDTO cancelPurchaseOrder(@PathVariable Long id) throws Exception {
        return salesService.cancelPO(id);
    }

    //Requirement PS8
    @PatchMapping("/po/{po_id}/plant_dispatched")
    public PurchaseOrderDTO plantDispatched(@PathVariable Long po_id) throws Exception {
        return salesService.plantDispatched(po_id);
    }

    //Requirement PS9
    @PatchMapping("/po/{po_id}/plant_delivered")
    public PurchaseOrderDTO plantDelivered(@PathVariable Long po_id) throws Exception {
        return salesService.plantDelivered(po_id);
    }

    //Requirement PS9
    @PatchMapping("/po/{po_id}/plant_rejected")
    public PurchaseOrderDTO plantRejected(@PathVariable Long po_id) throws Exception {
        return salesService.plantRejected(po_id);
    }

    //Requirement PS10
    @PatchMapping("/po/{po_id}/plant_returned")
    public PurchaseOrderDTO plantReturned(@PathVariable Long po_id) throws Exception {
        return salesService.plantReturned(po_id);
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

    //Requirement PS10
    @PatchMapping("/po/{po_id}/remittance")
    public String addRemittance(@PathVariable Long po_id) throws Exception {
        System.out.println("controller");
        return salesService.addRemittance(po_id);
    }


    //Requirement PS5 All POs
    @GetMapping("/po")
    @ResponseStatus(HttpStatus.OK)
    public List<PurchaseOrderDTO> fetchSubmittedPOs(@RequestParam String custName) {
        return salesService.findSubmittedPOs(custName);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        System.out.println("ERROR BEING HANDLED: "+ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.BAD_REQUEST);
    }






}