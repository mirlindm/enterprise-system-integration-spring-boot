package com.buildit.rental.application.dto;

import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class PlantInventoryEntryDTO extends RepresentationModel<PlantInventoryEntryDTO> {
    Long _id;
    String name;
    String description;
    BigDecimal price;
    BigDecimal totalPrice;


    Link _links;

    @JsonProperty("_links")
    public void setLinkss(final Map<String, Link> linksm) {
        linksm.forEach((label, link) ->  this.set_links(link.withRel(label))) ;
    }

    public PlantInventoryEntry toPlantInventoryEntry(PlantHireRequestDTO plantHireRequestDTO, String GET_ENTRY_URI){
        return PlantInventoryEntry.of(plantHireRequestDTO, GET_ENTRY_URI);
    }
}
