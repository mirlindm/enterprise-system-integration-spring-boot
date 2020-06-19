package com.buildit.integration;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.POStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;
import org.springframework.hateoas.Link;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RentITPurchaseOrderDTO {
    private Long id;
    private POStatus status;
    private PlantInventoryEntryDTO plantEntry;



    private BusinessPeriod rentalPeriod;


    private List<Link> links;

    private BigDecimal total;
    private String customerCompany;
    private List<Object> reservations;
    private String issueDate;
    private String paymentSchedule;


    public PurchaseOrderDTO toPurchaseOrder(RentITPurchaseOrderDTO rPoDto){
        PurchaseOrderDTO poDto = new PurchaseOrderDTO();
        poDto.setId(rPoDto.getId());
        poDto.setPlantEntry(rPoDto.getPlantEntry());
        poDto.setTotal(rPoDto.getTotal());
        poDto.setRentalPeriod(rPoDto.getRentalPeriod());
        poDto.setStatus(rPoDto.getStatus());
        return poDto;
    }

}
