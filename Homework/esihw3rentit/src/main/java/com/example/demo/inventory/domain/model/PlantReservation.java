package com.example.demo.inventory.domain.model;

import com.example.demo.maintenance.domain.model.MaintenancePlan;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor(force=true,access= AccessLevel.PUBLIC)
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

    public static PlantReservation of(PlantInventoryItem item, BusinessPeriod schedule) {
        PlantReservation reservation = new PlantReservation();
        reservation.plant = item;
        reservation.schedule = schedule;
        return reservation;
    }
}
