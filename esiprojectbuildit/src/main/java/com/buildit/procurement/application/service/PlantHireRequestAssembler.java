package com.buildit.procurement.application.service;

import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.rest.ProcurementRestController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class PlantHireRequestAssembler extends RepresentationModelAssemblerSupport<PlantHireRequest, PlantHireRequestDTO> {
    public PlantHireRequestAssembler(){
        super(ProcurementRestController.class, PlantHireRequestDTO.class);
    }
    @Override
    public PlantHireRequestDTO toModel(PlantHireRequest plantHireRequest) {
        PlantHireRequestDTO dto = createModelWithId(plantHireRequest.getId(), plantHireRequest);
        dto.setEntryId(plantHireRequest.getEntry().getId());
        dto.setComment(plantHireRequest.getComment());
        dto.setRentalPeriod(plantHireRequest.getRentalPeriod().toDTO());
        dto.setEntryName(plantHireRequest.getEntry().getName());
        dto.setNameOfConstructionSite(plantHireRequest.getNameOfConstructionSite());
        dto.setNameOfSiteEngineer(plantHireRequest.getNameOfSiteEngineer());
        dto.setSupplier(plantHireRequest.getSupplier());
        dto.setStatus(plantHireRequest.getStatus().toString());
        dto.setTotalCost(plantHireRequest.getTotalCost());
        dto.setId(plantHireRequest.getId());
        return dto;
    }
}
