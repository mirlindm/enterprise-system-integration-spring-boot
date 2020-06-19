package com.buildit.maintenance.application.dto;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.maintenance.domain.model.MaintenanceOrder;
import com.buildit.maintenance.domain.model.PlantInventoryItem;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class MaintenanceRequestDTO extends RepresentationModel<MaintenanceRequestDTO> {
    private Long _id;
    private BusinessPeriod expectedPeriod;
    private String siteEngineerName;
    private Long constructionSiteId;
    private String description;
    private long plantID;
    private String status;
    private MaintenanceOrder mo;
}
