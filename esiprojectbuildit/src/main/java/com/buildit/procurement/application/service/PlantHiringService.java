package com.buildit.procurement.application.service;

import com.buildit.common.application.BusinessPeriodValidator;
import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.integration.IntegrationService;
import com.buildit.integration.PlantAvailabilityRequestDTO;
import com.buildit.integration.PlantSophio;
import com.buildit.integration.RentITPurchaseOrderDTO;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.procurement.domain.repositories.PlantInventoryEntryRepository;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.rental.domain.model.PurchaseOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.table.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PlantHiringService{
    private static final String GET_ENTRY_URI = "http://localhost:8090/api/plants/";
    private static final String GET_ENTRY_URI_T9="http://rentit-heroku.herokuapp.com/api/inventory/";

    @Autowired
    RentalService rentalService;

    @Autowired
    PlantHireRequestRepository plantHireRequestRepository;

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PlantHireRequestAssembler plantHireRequestAssembler;

    @Autowired
    IntegrationService integrationService;

    //Requirements CC1
    public PlantHireRequestDTO createPlantHireRequest(PlantHireRequestDTO plantHireRequestDTO) throws Exception{

        BusinessPeriod businessPeriod = BusinessPeriod.of(
                plantHireRequestDTO.getRentalPeriod().getStartDate(),
                plantHireRequestDTO.getRentalPeriod().getEndDate());

        DataBinder binder = new DataBinder(businessPeriod);
        binder.addValidators(new BusinessPeriodValidator());
        binder.validate();

        if (binder.getBindingResult().hasErrors())
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Bad Interval");
        
        // We get plantEntry from rentit doesn't work until rentit implementation

        PlantInventoryEntry entry = null;
        PlantInventoryEntryDTO entryDTOFromRentit = null;
        PlantSophio plantT9 = null;
        BigDecimal total = new BigDecimal(0);;
            if(plantHireRequestDTO.getSupplier().equals("RentIT")) {
                PlantInventoryEntryDTO entryDTO = new PlantInventoryEntryDTO();
                entry = entryDTO.toPlantInventoryEntry(plantHireRequestDTO, GET_ENTRY_URI);
                plantInventoryEntryRepository.save(entry);
                entryDTOFromRentit = rentalService.getPlant(entry.get_xlink());
                total = calculatePrice(entryDTOFromRentit, businessPeriod);
            }else if(plantHireRequestDTO.getSupplier().equals("RentITT9")){

                PlantInventoryEntryDTO entryDTO = new PlantInventoryEntryDTO();
                entry = entryDTO.toPlantInventoryEntry(plantHireRequestDTO, GET_ENTRY_URI_T9);
                plantInventoryEntryRepository.save(entry);
                plantT9 = rentalService.getPlantT9(entry.get_xlink());
                PlantInventoryEntryDTO dtoT9 = new PlantInventoryEntryDTO();
                dtoT9.set_id(plantT9.get_id());
                dtoT9.setDescription(plantT9.getDescription());
                dtoT9.setName(plantT9.getName());
                dtoT9.setPrice(plantT9.getPrice());
                total = calculatePrice(dtoT9, businessPeriod);
            }

        //PlantInventoryEntryDTO entryDTOFromRentit = rentalService.getPlant(entry.get_xlink());
        
        PurchaseOrder po = new PurchaseOrder();
        PHRStatus status = PHRStatus.PENDING;
        PlantHireRequest phr = plantHireRequestDTO.toPlantHireRequest(status, total, entry, plantHireRequestDTO, po);
        phr = plantHireRequestRepository.save(phr);
        System.out.println("BBBBBBBBBBBBBBBBBBB"+ phr);
        return plantHireRequestAssembler.toModel(phr);
    }

    // Requirement CC5 & CC6 - Approve Plant Hire Request and automatically create a PO
    public PlantHireRequestDTO approvePlantHireRequest(Long plantHireRequestId, PlantHireRequestDTO plantHireRequestDTO) throws Exception {
        PlantHireRequest phr = plantHireRequestRepository.findById(plantHireRequestId).orElse(null);
        if (phr.getStatus().equals(PHRStatus.PENDING)) {
            phr.setComment(plantHireRequestDTO.getComment());
            plantHireRequestRepository.save(phr);
            // Needs more code to create a PO

            if(phr.getSupplier().equals("RentIT")){
                PlantInventoryEntryDTO entryDTO = rentalService.getPlant(phr.getEntry().get_xlink());
                System.out.println("Entry Details:" + entryDTO);
                PurchaseOrderDTO poDTO = rentalService.createPurchaseOrder(entryDTO, phr);
            }else if(phr.getSupplier().equals("RentITT9")){
                PlantSophio plantT9 = rentalService.getPlantT9(phr.getEntry().get_xlink());
                PlantInventoryEntryDTO dtoT9 = new PlantInventoryEntryDTO();
                dtoT9.set_id(plantT9.get_id());
                dtoT9.setDescription(plantT9.getDescription());
                dtoT9.setName(plantT9.getName());
                dtoT9.setPrice(plantT9.getPrice());
                PurchaseOrderDTO poDTO = rentalService.createPurchaseOrder(dtoT9, phr);
            }

            return plantHireRequestAssembler.toModel(phr);
        }
        else{
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "You can't approve the Hire Request in this stage");
        }
    }
    // Requirement CC5 - Reject Plant Hire Request
    public PlantHireRequestDTO rejectPlantHireRequest(Long plantHireRequestId, PlantHireRequestDTO plantHireRequestDTO) throws Exception{
        PlantHireRequest phr = plantHireRequestRepository.findById(plantHireRequestId).orElse(null);
        if (phr.getStatus().equals(PHRStatus.PENDING)) {
            phr.setStatus(PHRStatus.REJECTED);
            phr.setComment(plantHireRequestDTO.getComment());
            plantHireRequestRepository.save(phr);
            return plantHireRequestAssembler.toModel(phr);
        } else {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "You can't reject the Hire Request in this stage");

        }
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

    public List<PlantHireRequestDTO> getAllPlantHires(){
        ArrayList<PlantHireRequestDTO> response = new ArrayList<>();
        List<PlantHireRequest> allRequests = plantHireRequestRepository.findAll();
        for(PlantHireRequest req: allRequests){
            response.add(plantHireRequestAssembler.toModel(req));
        }
        return response;
    }

    //Requirements CC4
    public PlantHireRequestDTO getPlantHireRequest(Long id){
        PlantHireRequest plantHireRequest = plantHireRequestRepository.getOne(id);
        return plantHireRequestAssembler.toModel(plantHireRequest);
    }

    //Requirements CC2
    public PlantHireRequestDTO updatePlantHireRequest(Long id, PlantHireRequestDTO plantHireRequestDTO) throws Exception {
        PlantHireRequest plantHireRequest = plantHireRequestRepository.getOne(id);
        if(plantHireRequest.getStatus().toString() != "PENDING"){
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "You can't update the Hire Request in this stage");
        }

        BusinessPeriod businessPeriod = BusinessPeriod.of(
                plantHireRequestDTO.getRentalPeriod().getStartDate(),
                plantHireRequestDTO.getRentalPeriod().getEndDate());

        DataBinder binder = new DataBinder(businessPeriod);
        binder.addValidators(new BusinessPeriodValidator());
        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Bad Interval");
        }

        plantHireRequest.setNameOfSiteEngineer(plantHireRequestDTO.getNameOfSiteEngineer());
        plantHireRequest.setNameOfConstructionSite(plantHireRequestDTO.getNameOfConstructionSite());
        plantHireRequest.setSupplier(plantHireRequestDTO.getSupplier());
        plantHireRequest.setComment(plantHireRequestDTO.getComment());

        plantHireRequest.setRentalPeriod(businessPeriod);

        if(plantHireRequest.getEntry().getName() != plantHireRequestDTO.getEntryName()) {
            PlantInventoryEntryDTO entryDTO = new PlantInventoryEntryDTO();
            PlantInventoryEntry entry = entryDTO.toPlantInventoryEntry(plantHireRequestDTO, GET_ENTRY_URI);
            plantHireRequest.setEntry(entry);
            plantInventoryEntryRepository.save(entry);
            plantHireRequest.setEntry(entry);
        }
        System.out.println("===> passed if");
        // We get plantEntry from rentit doesn't work until rentit implementation
        try {
            PlantInventoryEntryDTO entryDTOFromRentit = rentalService.getPlant(plantHireRequest.getEntry().get_xlink());
            plantHireRequest.setTotalCost(calculatePrice(entryDTOFromRentit, businessPeriod));

        }
        catch(Exception e){
            plantHireRequest.setTotalCost(new BigDecimal(0));
        }
        PlantHireRequest pp = plantHireRequestRepository.save(plantHireRequest);
        return plantHireRequestAssembler.toModel(pp);
    }

    //Requirements CC3
    public PlantHireRequestDTO cancelPlantHireRequest(Long id) throws Exception {
        PlantHireRequest plantHireRequest = plantHireRequestRepository.findById(id).orElse(null);
        System.out.println(plantHireRequest);
        if(plantHireRequest.getStatus() != PHRStatus.PENDING && plantHireRequest.getStatus() != PHRStatus.ACCEPTED){
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "You can't cancel the plant hire at this stage!");
        }

        if(LocalDate.now().isAfter(plantHireRequest.getRentalPeriod().getStartDate() ) ) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "You can't cancel the Hire Request in this stage");
        }

        if(plantHireRequest.getPo() != null){
            plantHireRequest.setStatus(PHRStatus.PENDING_CANCELLATION);

            // Extra code needed for sending to RentIT
            if(plantHireRequest.getSupplier().equals("RentIT")){
                restTemplate.delete(plantHireRequest.getPo().get_xlink().getHref() + "/cancel");
                plantHireRequest.setStatus(PHRStatus.CANCELED);
                plantHireRequestRepository.save(plantHireRequest);
            }else if(plantHireRequest.getSupplier().equals("RentITT9")){

                //Fetch Purchase order
                RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
                template.patchForObject(plantHireRequest.getPo().get_xlink().getHref() + "/cancel", plantHireRequest, Object.class);
                plantHireRequest.setStatus(PHRStatus.CANCELED);
                plantHireRequestRepository.save(plantHireRequest);
            }

//            restTemplate.delete(plantHireRequest.getPo().get_xlink().getHref() + "/cancel");
//                plantHireRequest.setStatus(PHRStatus.CANCELED);
//                plantHireRequestRepository.save(plantHireRequest);
        }
        else {
            plantHireRequest.setStatus(PHRStatus.CANCELED);
        }
        plantHireRequestRepository.save(plantHireRequest);

        return plantHireRequestAssembler.toModel(plantHireRequest);
    }


    //Requirements CC8
    public PurchaseOrderDTO extendPlantHireRequest(Long id, PlantHireRequestDTO plantHireRequestDTO) throws Exception {
        PlantHireRequest plantHireRequest = plantHireRequestRepository.findById(id).orElse(null);
        System.out.println(plantHireRequest);
        if (plantHireRequest.getStatus() == PHRStatus.PENDING) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Your PlantHire Request is still Pending, there is no Purchase order created!");
        }

        if (plantHireRequest.getStatus() == PHRStatus.ACCEPTED & plantHireRequest.getPo() != null) {
            BusinessPeriod businessPeriod = BusinessPeriod.of(
                    plantHireRequest.getRentalPeriod().getStartDate(),
                    plantHireRequestDTO.getRentalPeriod().getEndDate());

            DataBinder binder = new DataBinder(businessPeriod);
            binder.addValidators(new BusinessPeriodValidator());
            binder.validate();

            if (binder.getBindingResult().hasErrors())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Interval");

            System.out.println("XXXXXXXXXXXXXXXXXXXXXX"+ plantHireRequestDTO);

            PurchaseOrderDTO purchaseOrderDTO = rentalService.extendPurchaseOrder(plantHireRequest,plantHireRequestDTO);
            plantHireRequest.setRentalPeriod(businessPeriod);
            plantHireRequestRepository.save(plantHireRequest);
            return purchaseOrderDTO;

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is Problem with extending this PO!");
        }
    }


}