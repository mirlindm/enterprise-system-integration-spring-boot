package com.buildit.procurement.application.dto;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.common.domain.model.BusinessPeriodDTO;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
public class PlantHireRequestDTO extends RepresentationModel<PlantHireRequestDTO> {
    private Long entryId;
    private Long id;
    private String entryName;
    private String nameOfSiteEngineer;
    private String status;
    private String nameOfConstructionSite;
    private String supplier;
    private String comment;
    private BigDecimal totalCost;
    private BusinessPeriodDTO rentalPeriod;

    public PlantHireRequest toPlantHireRequest(PHRStatus status, BigDecimal total, PlantInventoryEntry entry , PlantHireRequestDTO plantHireRequestDTO, PurchaseOrder po){
        return PlantHireRequest.of(status, total, entry , plantHireRequestDTO, po);
    }

}
