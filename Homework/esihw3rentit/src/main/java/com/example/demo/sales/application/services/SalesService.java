package com.example.demo.sales.application.services;

import com.example.demo.common.application.dto.BusinessPeriodValidator;
import com.example.demo.common.application.exception.PlantNotFoundException;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

@Service
public class SalesService {

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PlantReservationRepository plantReservationRepository;

    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    @Autowired
    InventoryService inventoryService;

    public PurchaseOrderDTO createPO (PurchaseOrderDTO poDTO) throws Exception {
        BusinessPeriod period = BusinessPeriod.of(poDTO.getRentalPeriod().getStartDate(), poDTO.getRentalPeriod().getEndDate());

        DataBinder binder = new DataBinder(period);
        binder.addValidators(new BusinessPeriodValidator());
        binder.validate();

        if (binder.getBindingResult().hasErrors())
            throw new Exception("Invalid Interval");

        if(poDTO.getPlant() == null)
            throw new Exception("Invalid Plant Inventory");

        PlantInventoryEntry plant = inventoryService.findEntryById(poDTO.getPlant().get_id());

        if(plant == null)
            throw new Exception("Plant NOT Found");

        PurchaseOrder po = PurchaseOrder.of(plant, period);

        Iterator<PlantInventoryItemDTO> availableItems = inventoryService.findAvailableItems(
                plant.getName(),
                poDTO.getRentalPeriod().getStartDate(),
                poDTO.getRentalPeriod().getEndDate()).iterator();

        if(!availableItems.hasNext()) {
            po.setStatus(POStatus.REJECTED);
            throw new Exception("NO available items");
        }

        PlantInventoryItem item = inventoryService.findItemById(availableItems.next().get_id());

        PlantReservation reservation = PlantReservation.of(item, po.getRentalPeriod());
        plantReservationRepository.save(reservation);
        po.getReservations().add(reservation);

        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toModel(po);
    }

    public PurchaseOrderDTO findPO(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElse(null);
        return purchaseOrderAssembler.toModel(po);
    }

    public PurchaseOrderDTO rejectPO(Long id) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElse(null);
        validatePO(po);
        while (!po.getReservations().isEmpty())
            plantReservationRepository.delete(po.getReservations().remove(0));
        po.setStatus(POStatus.CLOSED);
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toModel(po);
    }

    public PurchaseOrderDTO acceptPO(Long id) throws Exception {
        PurchaseOrder po =purchaseOrderRepository.findById(id).orElse(null);
        validatePO(po);
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toModel(po);
    }

    private void validatePO(PurchaseOrder po) throws Exception {
        if(po == null)
            throw new Exception("PO Not Found");
        if(po.getStatus() != POStatus.PENDING)
            throw new Exception("PO cannot be accepted/rejected due to it is not Pending");
    }

    @ExceptionHandler(PlantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handPlantNotFoundException(PlantNotFoundException ex) {
    }
}
