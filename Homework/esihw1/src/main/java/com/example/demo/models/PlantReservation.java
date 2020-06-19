package com.example.demo.models;

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
