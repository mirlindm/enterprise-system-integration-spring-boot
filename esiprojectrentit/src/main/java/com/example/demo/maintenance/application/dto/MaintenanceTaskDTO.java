package com.example.demo.maintenance.application.dto;

import com.example.demo.inventory.domain.model.*;
import com.example.demo.maintenance.domain.model.MaintenancePlan;
import com.example.demo.maintenance.domain.model.TypeOfWork;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
public class MaintenanceTaskDTO extends RepresentationModel<MaintenanceTaskDTO> {
    Long _id;
    String description;
    TypeOfWork type_of_work;
    BigDecimal total;
    BusinessPeriod rentalPeriod;
    PlantReservation reservation;
    MaintenancePlan maintenancePlan;
}
