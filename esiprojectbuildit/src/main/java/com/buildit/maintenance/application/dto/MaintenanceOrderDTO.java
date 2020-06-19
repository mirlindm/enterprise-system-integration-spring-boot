package com.buildit.maintenance.application.dto;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.domain.model.POStatus;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MaintenanceOrderDTO extends RepresentationModel<MaintenanceOrderDTO> {
    private Long id;
    private String description;
    private long constructionSiteId;
    private BusinessPeriod expectedPeriod;
    private Links _links;
    private String status;
    private String siteEngineerName;
    private long plantID;
}
