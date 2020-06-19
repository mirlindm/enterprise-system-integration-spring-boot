package com.example.demo.inventory.domain.model;

import com.example.demo.common.rest.ResourceSupport;
import com.example.demo.inventory.application.dto.MaintenanceTaskDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaintenanceTaskRequest extends ResourceSupport {
    Long _id;
    String description;
    TypeOfWork type_of_work;
    BigDecimal total;
    BusinessPeriod rentalPeriod;
    Long plant_id;

    public MaintenanceTask toMaintenanceTask(BusinessPeriod period, PlantReservation plantReservation, MaintenancePlan maintenancePlan){
        MaintenanceTaskDTO dto = new MaintenanceTaskDTO();

        dto.setDescription(this.getDescription());
        dto.setType_of_work(this.getType_of_work());
        dto.setTotal(this.getTotal());
//        dto.setRentalPeriod(period);
//        dto.setReservation(plantReservation);
//        dto.setMaintenancePlan(maintenancePlan);

        return MaintenanceTask.of(maintenancePlan, plantReservation, period, dto);
    }
}
