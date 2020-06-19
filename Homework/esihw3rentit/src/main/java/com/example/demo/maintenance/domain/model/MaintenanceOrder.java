package com.example.demo.maintenance.domain.model;

import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.maintenance.application.dto.MaintenanceOrderDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@Entity
@NoArgsConstructor(force=true,access= AccessLevel.PUBLIC)
public class MaintenanceOrder {
    @Id
    @GeneratedValue
    Long id;

    String description;

    //Long construction_site_id;

    Long constructionSiteId;

    //String site_engineer_name;

    String siteEngineerName;

    LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    MaintOrderStatus status;

    @Embedded
    BusinessPeriod expectedPeriod;

    @ManyToOne
    PlantInventoryItem plant;


    public static MaintenanceOrder of(PlantInventoryItem plant, BusinessPeriod period, MaintenanceOrderDTO maintenanceOrderDTO) {
        MaintenanceOrder mo = new MaintenanceOrder();
        mo.constructionSiteId = maintenanceOrderDTO.getConstructionSiteId();
        mo.description = maintenanceOrderDTO.getDescription();
        mo.expectedPeriod = period;
        mo.issueDate = LocalDate.now();
        mo.siteEngineerName = maintenanceOrderDTO.getSiteEngineerName();
        mo.status = MaintOrderStatus.PENDING;
        mo.plant = plant;
        return mo;
    }

}
