package com.buildit.procurement.rest;

import com.buildit.maintenance.application.dto.MaintenanceRequestDTO;
import com.buildit.maintenance.domain.model.MaintenanceRequest;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.application.service.PlantHiringService;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.services.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/procurements")
public class ProcurementRestController {

    @Autowired
    RentalService rentalService;

    @Autowired
    PlantHiringService plantHiringService;

    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @GetMapping("/plants")
    //Requirement PS1 - fetch plants from RentIt
    public List<PlantInventoryEntryDTO> findAvailablePlants(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate
    ) {
        return rentalService.findAvailablePlants(name,startDate.get(), endDate.get());

    }

    //Requirements CC1
    @PreAuthorize("hasAnyRole('SITE', 'ADMIN')")
    @PostMapping("/hire")
    public ResponseEntity<PlantHireRequestDTO> createPlantHire(@RequestBody PlantHireRequestDTO plantHireRequestDTO) throws Exception {
        PlantHireRequestDTO dto = plantHiringService.createPlantHireRequest(plantHireRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        System.out.println("AAAAAAAAAAAAAAAAAA" + dto);
        return new ResponseEntity<>(dto, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @GetMapping("/hire")
    public List<PlantHireRequestDTO> listAllPlantHires() {
        return plantHiringService.getAllPlantHires();
    }

    //Requirements CC4
    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @GetMapping("/hire/{id}")
    public ResponseEntity<PlantHireRequestDTO> getPlantHireRequest(@PathVariable("id") Long id) {
        try {
            PlantHireRequestDTO dto = plantHiringService.getPlantHireRequest(id);
            return new ResponseEntity<PlantHireRequestDTO>(dto, null, HttpStatus.OK);
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PlantHire Not Found", exc);
        }
    }

    //Requirements CC2
    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @PutMapping("/hire/{id}")
    public ResponseEntity<PlantHireRequestDTO> updatePlantHire(@PathVariable("id") Long id, @RequestBody PlantHireRequestDTO plantHireRequestDTO) throws Exception {
        PlantHireRequestDTO dto = plantHiringService.updatePlantHireRequest(id, plantHireRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(dto, headers, HttpStatus.OK);
    }

    //Requirements CC3
    @PreAuthorize("hasAnyRole('SITE', 'ADMIN')")
    @DeleteMapping("/hire/{id}")
    public ResponseEntity<PlantHireRequestDTO> cancelPlantHire(@PathVariable("id") Long id) throws Exception {
        PlantHireRequestDTO dto = plantHiringService.cancelPlantHireRequest(id);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(dto, headers, HttpStatus.OK);
    }

    //Requirements CC5
    @PreAuthorize("hasAnyRole('WORKS', 'ADMIN')")
    @PutMapping("/hire/{id}/approve")
    public ResponseEntity<PlantHireRequestDTO> approvePlantHire(@PathVariable("id") Long id, @RequestBody PlantHireRequestDTO plantHireRequestDTO) throws Exception {
        PlantHireRequestDTO dto = plantHiringService.approvePlantHireRequest(id, plantHireRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(dto, headers, HttpStatus.OK);
    }

    //Requirements CC5
    @PreAuthorize("hasAnyRole('WORKS', 'ADMIN')")
    @PutMapping("/hire/{id}/reject")
    public ResponseEntity<PlantHireRequestDTO> rejectPlantHire(@PathVariable("id") Long id, @RequestBody PlantHireRequestDTO plantHireRequestDTO) throws Exception {
        PlantHireRequestDTO dto = plantHiringService.rejectPlantHireRequest(id, plantHireRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(dto, headers, HttpStatus.OK);
    }

    //Requirements CC8
    @PreAuthorize("hasAnyRole('SITE', 'ADMIN')")
    @PutMapping("/hire/{id}/extend")
    public ResponseEntity<PurchaseOrderDTO> extendPlantHire(@PathVariable("id") Long id, @RequestBody PlantHireRequestDTO plantHireRequestDTO) throws Exception {
        PurchaseOrderDTO poDTO = plantHiringService.extendPlantHireRequest(id, plantHireRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(poDTO, headers, HttpStatus.OK);
    }

}