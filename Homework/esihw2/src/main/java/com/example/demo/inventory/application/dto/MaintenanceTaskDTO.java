package com.example.demo.inventory.application.dto;

import com.example.demo.inventory.domain.model.*;
import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import java.math.BigDecimal;

@Data
public class MaintenanceTaskDTO extends ResourceSupport {
    Long _id;
    String description;
    TypeOfWork type_of_work;
    BigDecimal total;
    BusinessPeriod rentalPeriod;
    PlantReservation reservation;
    MaintenancePlan maintenancePlan;
}
