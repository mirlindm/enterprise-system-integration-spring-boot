package com.buildit.procurement.application.service;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.domain.repositories.PlantInventoryEntryRepository;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.rental.domain.model.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PlantHiringService{
    private static final String GET_ENTRY_URI = "http://localhost:8090/api/sales/plants/";

    @Autowired
    RentalService rentalService;

    @Autowired
    PlantHireRequestRepository plantHireRequestRepository;

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;

    public PlantHireRequest createPlantHireRequest(PlantHireRequestDTO plantHireRequestDTO) {
        PlantHireRequest plantHireRequest = new PlantHireRequest();
        plantHireRequest.setNameOfSiteEngineer(plantHireRequestDTO.getNameOfSiteEngineer());
        plantHireRequest.setNameOfConstructionSite(plantHireRequestDTO.getNameOfConstructionSite());

        BusinessPeriod businessPeriod = BusinessPeriod.of(
                plantHireRequestDTO.getRentalPeriod().getStartDate(),
                plantHireRequestDTO.getRentalPeriod().getEndDate());
        plantHireRequest.setRentalPeriod(businessPeriod);

        PlantInventoryEntry entry = PlantInventoryEntry.of(plantHireRequestDTO, GET_ENTRY_URI);
        plantHireRequest.setEntry(entry);
        plantInventoryEntryRepository.save(entry);
        plantHireRequest.setEntry(entry);

        // We get plantEntry from rentit doesn't work until rentit implementation
//        PlantInventoryEntryDTO entryDTOFromRentit = rentalService.getPlant(entry.get_xlink());
//        plantHireRequest.setTotalCost(calculatePrice(entryDTOFromRentit, businessPeriod));
        plantHireRequest.setTotalCost(new BigDecimal(900));
        plantHireRequest.setStatus(PHRStatus.PENDING);
        return plantHireRequestRepository.save(plantHireRequest);
    }

    public PurchaseOrder approvePlantHireRequest(Long plantHireRequestId) {
        PlantHireRequest phr = plantHireRequestRepository.findById(plantHireRequestId).orElse(null);
        if (phr.getStatus().equals(PHRStatus.PENDING)) {
            PlantInventoryEntryDTO entryDTO = rentalService.getPlant(phr.getEntry().get_xlink());
            return rentalService.createPurchaseOrder(entryDTO, phr);
        }
        return null;
    }

    public PlantHireRequest rejectPlantHireRequest(PlantHireRequestDTO plantHireRequestDTO, Long plantHireRequestId) {
        PlantHireRequest phr = plantHireRequestRepository.findById(plantHireRequestId).orElse(null);
        phr.setStatus(PHRStatus.REJECTED);
        phr.setComment(plantHireRequestDTO.getComment());
        return plantHireRequestRepository.save(phr);
    }

    public List<PlantHireRequest> getPlantHireRequests() {
        return plantHireRequestRepository.findAll();
    }

    public BigDecimal calculatePrice(PlantInventoryEntryDTO plantInventoryEntryDTO, BusinessPeriod rentalPeriod) {
        BigDecimal entryPrice = plantInventoryEntryDTO.getPrice();
        LocalDate startDate = rentalPeriod.getStartDate();
        LocalDate endDate = rentalPeriod.getEndDate();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return entryPrice.multiply(new BigDecimal(days));
    }

}