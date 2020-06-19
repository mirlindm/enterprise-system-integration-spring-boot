package com.buildit.procurement.application.dto;

import com.buildit.common.domain.model.BusinessPeriod;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class PlantHireRequestDTO extends RepresentationModel<PlantHireRequestDTO> {
    private String entryId;
    private String entryName;
    private String nameOfSiteEngineer;
    private String nameOfConstructionSite;
    private String comment;
    private BusinessPeriod rentalPeriod;
}
