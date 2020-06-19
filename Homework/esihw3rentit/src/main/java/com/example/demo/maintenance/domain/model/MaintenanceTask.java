package com.example.demo.maintenance.domain.model;

import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.maintenance.application.dto.MaintenanceTaskDTO;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

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

    @OneToOne
    MaintenanceOrder order;


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
