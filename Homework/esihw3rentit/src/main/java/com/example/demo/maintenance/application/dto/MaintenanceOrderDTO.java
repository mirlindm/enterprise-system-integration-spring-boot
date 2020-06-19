package com.example.demo.maintenance.application.dto;

import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.maintenance.domain.model.MaintOrderStatus;
import com.example.demo.maintenance.domain.model.MaintenanceOrder;
import com.example.demo.maintenance.domain.model.MaintenancePlan;
import com.example.demo.maintenance.domain.model.MaintenanceTask;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
public class MaintenanceOrderDTO extends RepresentationModel<MaintenanceOrderDTO>{
    Long id;
    String description;
    //Long construction_site_id;
    Long constructionSiteId;
    String siteEngineerName;
    LocalDate issueDate;
    MaintOrderStatus status;
    BusinessPeriod expectedPeriod;
    PlantInventoryItem plant;

//    public MaintenanceOrder toMaintenanceOrder(BusinessPeriod businessPeriod, Long constructionSiteId, String siteEngineerName, PlantInventoryItem plant ){
//
//        MaintenanceOrderDTO moDto = new MaintenanceOrderDTO();
//        moDto.setConstruction_site_id(constructionSiteId);
//        moDto.setSite_engineer_name(siteEngineerName);
//
//        return MaintenanceOrder.of(plant, businessPeriod, moDto);
//
//    }




}
