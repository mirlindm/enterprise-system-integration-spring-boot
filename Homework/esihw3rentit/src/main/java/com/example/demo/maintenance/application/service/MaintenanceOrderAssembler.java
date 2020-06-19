package com.example.demo.maintenance.application.service;


import com.example.demo.maintenance.application.dto.MaintenanceOrderDTO;
import com.example.demo.maintenance.application.dto.MaintenanceTaskDTO;
import com.example.demo.maintenance.domain.model.MaintenanceOrder;
import com.example.demo.maintenance.domain.model.MaintenanceTask;
import com.example.demo.maintenance.rest.MaintenanceRestController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceOrderAssembler extends RepresentationModelAssemblerSupport<MaintenanceOrder, MaintenanceOrderDTO> {

    public MaintenanceOrderAssembler(){  super(MaintenanceRestController.class, MaintenanceOrderDTO.class);}

    @Override
    public MaintenanceOrderDTO toModel(MaintenanceOrder maintenanceOrder){
        MaintenanceOrderDTO moDto = createModelWithId(maintenanceOrder.getId(),maintenanceOrder);
        moDto.setId(maintenanceOrder.getId());
        moDto.setConstructionSiteId(maintenanceOrder.getConstructionSiteId());
        moDto.setDescription(maintenanceOrder.getDescription());
        moDto.setExpectedPeriod(maintenanceOrder.getExpectedPeriod());
        moDto.setIssueDate(maintenanceOrder.getIssueDate());
        moDto.setSiteEngineerName(maintenanceOrder.getSiteEngineerName());
        moDto.setStatus(maintenanceOrder.getStatus());
        moDto.setPlant(maintenanceOrder.getPlant());

        return moDto;

    }

}
