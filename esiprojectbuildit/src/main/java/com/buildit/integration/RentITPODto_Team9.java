package com.buildit.integration;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.procurement.application.service.PlantHiringService;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.POStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.hateoas.Link;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class RentITPODto_Team9 {
    private Long _id;
    private POStatus status;
    private PlantInventoryEntryDTO plant;



    private BusinessPeriod rentalPeriod;

    private BigDecimal total;
    private BigDecimal _total;
    private String customerCompany;
    private List<Object> reservations;
    private String issueDate;
    private String paymentSchedule;
    private String nameOfConstructionSite;
    private String contactEmail;


    Link _links;

    @JsonProperty("_links")
    public void setLinkss(final Map<String, Link> linksm) {
        linksm.forEach((label, link) ->  this.set_links(link.withRel(label))) ;
    }

    public PurchaseOrderDTO toPurchaseOrder(RentITPODto_Team9 rPoDto){
        PurchaseOrderDTO poDto = new PurchaseOrderDTO();
        poDto.setId(rPoDto.get_id());
        poDto.setPlantEntry(rPoDto.getPlant());
        poDto.setTotal(rPoDto.getTotal());
        poDto.setRentalPeriod(rPoDto.getRentalPeriod());
        poDto.setStatus(rPoDto.getStatus());
        return poDto;
    }
}
