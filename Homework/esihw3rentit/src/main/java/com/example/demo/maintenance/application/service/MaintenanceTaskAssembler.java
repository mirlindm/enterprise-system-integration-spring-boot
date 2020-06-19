package com.example.demo.maintenance.application.service;

import com.example.demo.maintenance.application.dto.MaintenanceTaskDTO;
import com.example.demo.maintenance.domain.model.MaintenanceTask;
import com.example.demo.inventory.rest.InventoryRestController;
import com.example.demo.maintenance.rest.MaintenanceRestController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceTaskAssembler extends RepresentationModelAssemblerSupport<MaintenanceTask, MaintenanceTaskDTO> {
    public MaintenanceTaskAssembler(){
        super(MaintenanceRestController.class, MaintenanceTaskDTO.class);
    }

    @Override
    public MaintenanceTaskDTO toModel(MaintenanceTask maintenanceTask) {
        MaintenanceTaskDTO dto = createModelWithId(maintenanceTask.getId(), maintenanceTask);
        dto.set_id(maintenanceTask.getId());
        dto.setDescription(maintenanceTask.getDescription());
        dto.setMaintenancePlan(maintenanceTask.getMaintenancePlan());
        dto.setRentalPeriod(maintenanceTask.getRentalPeriod());
        dto.setTotal(maintenanceTask.getTotal());
        dto.setReservation(maintenanceTask.getReservation());
        dto.setType_of_work(maintenanceTask.getType_of_work());

        return dto;
    }
}

