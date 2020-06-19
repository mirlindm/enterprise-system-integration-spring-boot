package com.buildit.rental.rest;


import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.application.service.PlantHiringService;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.services.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rental")
public class RentalRestController {

    @Autowired
    RentalService rentalService;

    @Autowired
    PlantHiringService plantHiringService;

    //Requirement CC7
//    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @GetMapping("/po")
    public List<PurchaseOrderDTO> listAllPurchaseOrders() {
        return rentalService.getAllPurchaseOrders();
    }


}
