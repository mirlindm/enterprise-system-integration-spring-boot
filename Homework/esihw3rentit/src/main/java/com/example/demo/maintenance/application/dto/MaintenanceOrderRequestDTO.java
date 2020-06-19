package com.example.demo.maintenance.application.dto;

import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.maintenance.domain.model.MaintOrderStatus;
import com.example.demo.maintenance.domain.model.MaintenanceOrder;
import com.example.demo.maintenance.domain.model.MaintenanceTaskRequest;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
public class MaintenanceOrderRequestDTO extends RepresentationModel<MaintenanceOrderRequestDTO> {
    Long _id;
    Long constructionSiteId;
    String description;
    String siteEngineerName;
    BusinessPeriod expectedPeriod;
    Long plantID;

    public MaintenanceOrder toMaintenanceOrder(BusinessPeriod businessPeriod, Long constructionSiteId, String description, String siteEngineerName, PlantInventoryItem plant ){

        MaintenanceOrderDTO moDto = new MaintenanceOrderDTO();
        moDto.setConstructionSiteId(constructionSiteId);
        moDto.setDescription(description);
        moDto.setSiteEngineerName(siteEngineerName);

        return MaintenanceOrder.of(plant, businessPeriod, moDto);

    }
}
