package com.example.demo.models;

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

}
