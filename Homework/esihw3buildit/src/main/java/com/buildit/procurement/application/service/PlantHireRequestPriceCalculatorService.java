package com.buildit.procurement.application.service;

import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.common.domain.model.BusinessPeriod;

import java.math.BigDecimal;

public interface PlantHireRequestPriceCalculatorService {
     BigDecimal calculatePrice(PlantInventoryEntryDTO plantInventoryEntryDTO, BusinessPeriod rentalPeriod);
}
