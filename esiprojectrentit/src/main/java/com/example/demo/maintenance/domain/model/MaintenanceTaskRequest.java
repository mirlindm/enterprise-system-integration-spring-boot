package com.example.demo.maintenance.domain.model;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.maintenance.application.dto.MaintenanceTaskDTO;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
public class MaintenanceTaskRequest extends RepresentationModel<MaintenanceTaskRequest> {
    Long _id;
    String description;
    TypeOfWork type_of_work;
    BigDecimal total;
    BusinessPeriodDTO rentalPeriod;
    Long plant_id;
    MaintenanceOrder maintenanceOrder;

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
