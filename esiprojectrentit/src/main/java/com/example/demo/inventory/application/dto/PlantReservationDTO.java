package com.example.demo.inventory.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.maintenance.domain.model.MaintenancePlan;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlantReservationDTO {
    Long id;
    BusinessPeriodDTO schedule;
    PurchaseOrderDTO rental;
    PlantInventoryItemDTO plant;


    public PlantReservation toPlantReservation(PlantInventoryItem item, BusinessPeriod period){
        //PlantReservationDTO plantReservation= new PlantReservationDTO();

        return PlantReservation.of(item,period);
    }

}
