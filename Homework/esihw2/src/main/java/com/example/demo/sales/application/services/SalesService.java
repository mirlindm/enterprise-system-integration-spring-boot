package com.example.demo.sales.application.services;

import com.example.demo.common.application.dto.BusinessPeriodValidator;
import com.example.demo.common.application.exception.PlantNotFoundException;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Service
public class SalesService {

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    public PurchaseOrderDTO createPO (PurchaseOrderDTO poDTO) throws Exception {
        BusinessPeriod period = BusinessPeriod.of(poDTO.getRentalPeriod().getStartDate(), poDTO.getRentalPeriod().getEndDate());

        DataBinder binder = new DataBinder(period);
        binder.addValidators(new BusinessPeriodValidator());
        binder.validate();

        if (binder.getBindingResult().hasErrors())
            throw new Exception("Invalid Interval");

        if(poDTO.getPlant() == null)
            throw new Exception("Invalid Plant Inventory");

        PlantInventoryEntry plant = plantInventoryEntryRepository.findById(poDTO.getPlant().get_id()).orElse(null);

        if(plant == null)
            throw new Exception("Plant NOT Found");

        PurchaseOrder po = PurchaseOrder.of(plant, period);
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toResource(po);
    }

    public PurchaseOrderDTO findPO(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElse(null);
        return purchaseOrderAssembler.toResource(po);
    }

    @ExceptionHandler(PlantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handPlantNotFoundException(PlantNotFoundException ex) {
    }

    public PurchaseOrderDTO rejectPO(String id) {
        PurchaseOrder po =purchaseOrderRepository.findById(Long.parseLong(id)).orElse(null);
        po.rejectPO();
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toResource(po);
    }

    public PurchaseOrderDTO acceptPO(String  id) {
        PurchaseOrder po =purchaseOrderRepository.findById(Long.parseLong(id)).orElse(null);
        po.acceptPO();
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toResource(po);
    }
}
