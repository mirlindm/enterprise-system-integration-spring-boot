package com.example.demo.inventory.domain.model;

import com.example.demo.sales.domain.model.PurchaseOrder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PlantReservation {
    @Id
    @GeneratedValue
    Long id;

    @Embedded
    BusinessPeriod schedule;

    @ManyToOne
    PurchaseOrder rental;

    @ManyToOne
    PlantInventoryItem plant;

    @ManyToOne
    MaintenancePlan maint_plan;
}
