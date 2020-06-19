package com.buildit.rental.application.services;

import com.buildit.common.rest.ExtendedLink;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.PurchaseOrder;
import com.buildit.rental.domain.repositories.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class RentalService {
    private static final String BASE_URL = "http://localhost:8090/api";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PlantHireRequestRepository plantHireRequestRepository;

    public List<PlantInventoryEntryDTO> findAvailablePlants(String plantName, LocalDate startDate, LocalDate endDate) {
        PlantInventoryEntryDTO[] plants = restTemplate.getForObject(
                BASE_URL + "/sales/plants?name={name}&startDate={start}&endDate={end}",
                PlantInventoryEntryDTO[].class, plantName, startDate, endDate);
        return Arrays.asList(plants);
    }

    public PlantInventoryEntryDTO getPlant(ExtendedLink requestLink) {
        System.out.println(requestLink);
        return restTemplate.getForObject(requestLink.getHref(), PlantInventoryEntryDTO.class);
    }

    public PurchaseOrder createPurchaseOrder(PlantInventoryEntryDTO plantInventoryEntryDTO, PlantHireRequest plantHireRequest) {
        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
        purchaseOrderDTO.setPlant(plantInventoryEntryDTO);
        purchaseOrderDTO.setTotal(plantHireRequest.getTotalCost());
        purchaseOrderDTO.setRentalPeriod(plantHireRequest.getRentalPeriod());
        purchaseOrderDTO = restTemplate.postForObject(BASE_URL + "/sales/orders", purchaseOrderDTO, PurchaseOrderDTO.class);

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        System.out.println("QQQQQQQQQQQQQQQQQQQQQ" + purchaseOrderDTO);
        purchaseOrder.set_xlink(new ExtendedLink(purchaseOrderDTO.get_links().getHref(), "getPO", HttpMethod.GET));
        purchaseOrderRepository.save(purchaseOrder);
        plantHireRequest.setPo(purchaseOrder);
        plantHireRequest.setStatus(PHRStatus.ACCEPTED);
        plantHireRequestRepository.save(plantHireRequest);


        return purchaseOrder;
    }

}