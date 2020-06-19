package com.buildit.procurement.domain.model;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@Data
public class PlantHireRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private BusinessPeriod rentalPeriod;

    @Enumerated(EnumType.STRING)
    private PHRStatus status;

    private String nameOfSiteEngineer;

    private String nameOfConstructionSite;

    private String supplier;

    private String comment;

    @Column(precision = 8, scale = 2)
    private BigDecimal totalCost;

    @OneToOne
    private PlantInventoryEntry entry;

    @OneToOne
    private PurchaseOrder po;


    public static PlantHireRequest of(PHRStatus status, BigDecimal total, PlantInventoryEntry entry , PlantHireRequestDTO plantHireRequestDTO, PurchaseOrder po) {
        PlantHireRequest phr = new PlantHireRequest();
        phr.rentalPeriod = plantHireRequestDTO.getRentalPeriod().toBusinessPeriod();
        phr.supplier = plantHireRequestDTO.getSupplier();
        phr.comment = plantHireRequestDTO.getComment();
        phr.nameOfConstructionSite = plantHireRequestDTO.getNameOfConstructionSite();
        phr.nameOfSiteEngineer = plantHireRequestDTO.getNameOfSiteEngineer();
        phr.totalCost = total;
        phr.status = status;
        phr.entry = entry;
        return phr;
    }

}
