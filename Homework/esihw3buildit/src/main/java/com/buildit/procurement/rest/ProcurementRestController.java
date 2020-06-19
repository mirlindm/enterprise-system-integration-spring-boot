package com.buildit.procurement.rest;

import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.services.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/procurements")
public class ProcurementRestController {

    @Autowired
    RentalService rentalService;

    @GetMapping("/plants")
    public List<PlantInventoryEntryDTO> findAvailablePlants(
            @RequestParam(name = "name", required = false) Optional<String> plantName,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate
        ){
        return rentalService.findAvailablePlants(plantName.get(), startDate.get(), endDate.get());

    }
}
