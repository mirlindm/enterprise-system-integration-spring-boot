package com.example.demo.inventory.application.service;

import com.example.demo.inventory.application.dto.MaintenanceTaskDTO;
import com.example.demo.inventory.domain.model.MaintenanceTask;
import com.example.demo.inventory.rest.InventoryRestController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceTaskAssembler extends ResourceAssemblerSupport<MaintenanceTask, MaintenanceTaskDTO> {
    public MaintenanceTaskAssembler(){
        super(InventoryRestController.class, MaintenanceTaskDTO.class);
    }

    @Override
    public MaintenanceTaskDTO toResource(MaintenanceTask maintenanceTask) {
        MaintenanceTaskDTO dto = createResourceWithId(maintenanceTask.getId(), maintenanceTask);
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
