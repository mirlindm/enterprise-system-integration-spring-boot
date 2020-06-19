package com.example.demo.inventory.domain.model;

import com.example.demo.inventory.application.dto.MaintenanceTaskDTO;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@Entity
public class MaintenanceTask {

    @Id
    @GeneratedValue
    Long id;

    String description;

    @Enumerated(EnumType.STRING)
    TypeOfWork type_of_work;

    @Column(precision=8,scale=2)
    BigDecimal total;

    @Embedded
    BusinessPeriod rentalPeriod;

    @ManyToOne
    PlantReservation reservation;

    @ManyToOne(cascade={CascadeType.MERGE})
    MaintenancePlan maintenancePlan;


    public static MaintenanceTask of(MaintenancePlan maintenancePlan, PlantReservation plantReservation, BusinessPeriod period, MaintenanceTaskDTO maintenanceTaskDTO) {
        MaintenanceTask mt = new MaintenanceTask();
        mt.id= maintenanceTaskDTO.get_id();
        mt.description=maintenanceTaskDTO.getDescription();
        mt.rentalPeriod=period;
        mt.type_of_work=maintenanceTaskDTO.getType_of_work();
        mt.total=maintenanceTaskDTO.getTotal();
        mt.reservation=plantReservation;
        mt.maintenancePlan=maintenancePlan;
        return mt;
    }

}
