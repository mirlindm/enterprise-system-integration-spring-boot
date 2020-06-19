package com.buildit.rental.application.services;

import com.buildit.common.rest.ExtendedLink;
import com.buildit.integration.*;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.PurchaseOrder;
import com.buildit.rental.domain.repositories.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
//import sun.plugin.PluginURLJarFileCallBack;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @Autowired
    IntegrationService integrationService;


    //Requirement PS1 - fetch plants from RentIt
    public List<PlantInventoryEntryDTO> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
        //PlantInventoryEntryDTO[] plants = restTemplate.getForObject(BASE_URL + "/sales/plants?name={name}&startDate={start}&endDate={end}",
        //        PlantInventoryEntryDTO[].class, plantName, startDate, endDate);

        //System.out.println("XXXXXXXXXXXXXXXXXXXXXXX"+integrationService.findPlants(LocalDate.now(), LocalDate.now().plusDays(20)));

        PlantAvailabilityRequestDTO plantAvailabilityRequestDTO = new PlantAvailabilityRequestDTO();
        plantAvailabilityRequestDTO.setEndDate(endDate);
        plantAvailabilityRequestDTO.setStartDate(startDate);
        plantAvailabilityRequestDTO.setName(name);  // Need to update the method to take the name, since it is needed for the other system

        List<CollectionModel> collectionModels  = integrationService.findPlants(plantAvailabilityRequestDTO);

        List objList =  Arrays.asList(collectionModels.get(0).getContent().toArray());
//        List<PlantInventoryEntryDTO> plants = (List<PlantInventoryEntryDTO>) objList.get(0);

        List<PlantInventoryEntryDTO> plantsFinal = new ArrayList<>() ;

        for(Plant i:(List<Plant>)objList.get(0)){
            PlantInventoryEntryDTO plantInventoryEntryDTO = new PlantInventoryEntryDTO();
            plantInventoryEntryDTO.setName(i.getName());
            plantInventoryEntryDTO.set_id(i.get_id());
            plantInventoryEntryDTO.setDescription(i.getDescription());
            plantInventoryEntryDTO.setPrice(i.getPrice());
            plantInventoryEntryDTO.setTotalPrice(i.getTotalPrice());
            plantInventoryEntryDTO.add(i.getLinks());
            plantsFinal.add(plantInventoryEntryDTO);
        }


        List<PlantSophio> plantsFromOtherSystem = (List<PlantSophio>) objList.get(1);

        for(PlantSophio i:plantsFromOtherSystem){
            PlantInventoryEntryDTO plantInventoryEntryDTO = new PlantInventoryEntryDTO();
            plantInventoryEntryDTO.setName(i.getName());
            plantInventoryEntryDTO.set_id(i.get_id());
            plantInventoryEntryDTO.setDescription(i.getDescription());
            plantInventoryEntryDTO.setPrice(i.getPrice());
            plantInventoryEntryDTO.add(i.get_links());
            plantsFinal.add(plantInventoryEntryDTO);
        }

//        System.out.println("Extracted Plants  " +plants);
        return plantsFinal;
    }

    public PlantInventoryEntryDTO getPlant(ExtendedLink requestLink) {
        System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"+ requestLink.getHref());
        return restTemplate.getForObject(requestLink.getHref(), PlantInventoryEntryDTO.class);
    }

    public PlantSophio getPlantT9(ExtendedLink requestLink) {
        System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"+ requestLink.getHref());
        return restTemplate.getForObject(requestLink.getHref(), PlantSophio.class);
    }

    public PurchaseOrderDTO createPurchaseOrder(PlantInventoryEntryDTO plantInventoryEntryDTO, PlantHireRequest plantHireRequest) {
        System.out.println("PHRRRR" +plantHireRequest);

        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
        purchaseOrderDTO.setPlantEntry(plantInventoryEntryDTO);
        purchaseOrderDTO.setTotal(plantHireRequest.getTotalCost());
        purchaseOrderDTO.setRentalPeriod(plantHireRequest.getRentalPeriod());
        purchaseOrderDTO.setCustomerCompany("BUILDIT");

        System.out.println("Supplliieer name" +plantHireRequest.getSupplier());

        if(plantHireRequest.getSupplier().equals("RentIT")) {

            System.out.println("First iffff");

            purchaseOrderDTO = restTemplate.postForObject(BASE_URL + "/sales/po", purchaseOrderDTO, PurchaseOrderDTO.class);


            PurchaseOrder purchaseOrder = new PurchaseOrder();
            System.out.println("QQQQQQQQQQQQQQQQQQQQQ" + purchaseOrderDTO);
            purchaseOrder.set_xlink(new ExtendedLink("http://localhost:8090/api/sales/po/" + purchaseOrderDTO.getId(), "getPO", HttpMethod.GET));

            purchaseOrder.setReferenceID(purchaseOrderDTO.getId());

            purchaseOrderRepository.save(purchaseOrder);
            plantHireRequest.setPo(purchaseOrder);

        }else if(plantHireRequest.getSupplier().equals("RentITT9")){

            System.out.println("TTTTTTTTT" + purchaseOrderDTO);
            RentITPODto_Team9 purchaseOrderDTOT9 = new RentITPODto_Team9();

            purchaseOrderDTOT9.setPlant(plantInventoryEntryDTO);
            purchaseOrderDTOT9.set_total(plantHireRequest.getTotalCost());
            purchaseOrderDTOT9.setRentalPeriod(plantHireRequest.getRentalPeriod());
            purchaseOrderDTOT9.setCustomerCompany("BUILDIT");
            purchaseOrderDTOT9.setContactEmail("builditT6@gmail.com");
            purchaseOrderDTOT9.setNameOfConstructionSite("http://buildit-app-test.herokuapp.com/api/payables/invoices\n");

            RentITPODto_Team9 poT9 = restTemplate.postForObject("http://rentit-heroku.herokuapp.com/api/sales/orders/", purchaseOrderDTOT9, RentITPODto_Team9.class);


            PurchaseOrder purchaseOrder = new PurchaseOrder();
            System.out.println("QQQQQQQQQQQQQQQQQQQQQ" + purchaseOrderDTOT9);
            purchaseOrder.set_xlink(new ExtendedLink("http://rentit-heroku.herokuapp.com/api/sales/orders/" + poT9.get_id(), "getPO", HttpMethod.GET));

            purchaseOrder.setReferenceID(purchaseOrderDTO.getId());

            purchaseOrderRepository.save(purchaseOrder);
            plantHireRequest.setPo(purchaseOrder);
        }

        plantHireRequest.setStatus(PHRStatus.ACCEPTED);
        plantHireRequestRepository.save(plantHireRequest);

        return purchaseOrderDTO;
    }

    // Requirement CC 7
    public List<PurchaseOrderDTO> getAllPurchaseOrders(){
        System.out.println("===> getAllPurchaseOrders");
        ArrayList<PurchaseOrderDTO> response = new ArrayList<>();
//        List<PurchaseOrder> allOrders = purchaseOrderRepository.findAll();
        List<CollectionModel> pColModel = integrationService.getPO("BUILDIT");

        List objList =  Arrays.asList(pColModel.get(0).getContent().toArray());
        List<RentITPurchaseOrderDTO> newDTOs = (List<RentITPurchaseOrderDTO>) objList.get(0);

        for(RentITPurchaseOrderDTO rDtos : newDTOs){
            response.add(rDtos.toPurchaseOrder(rDtos));
        }


        List<RentITPODto_Team9> newDTOT9 = (List<RentITPODto_Team9>) objList.get(1);

        for(RentITPODto_Team9 rDtoT9 : newDTOT9){
            response.add(rDtoT9.toPurchaseOrder(rDtoT9));
        }


        //PurchaseOrderDTO poDTO = new PurchaseOrderDTO();
        //poDTO.setPlantEntry();
//        for(PurchaseOrder order: allOrders){
//            System.out.println("MIRLIND    MIRLIND" + order.get_xlink().getHref());
//            response.add(restTemplate.getForObject(order.get_xlink().getHref(), PurchaseOrderDTO.class));
//        }

        return response;
    }

    public PurchaseOrderDTO extendPurchaseOrder( PlantHireRequest phr, PlantHireRequestDTO plantHireRequestDTO) throws Exception {
        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
        RentITPODto_Team9 purchaseOrderDTOT9= new RentITPODto_Team9();
        try {
            System.out.println("extension hire"+ plantHireRequestDTO);
            System.out.println("extension hire po"+ phr);
            if(phr.getSupplier().equals("RentIT")) {
                restTemplate.put(phr.getPo().get_xlink().getHref() + "/extend", plantHireRequestDTO, PurchaseOrderDTO.class);
                purchaseOrderDTO = restTemplate.getForObject(phr.getPo().get_xlink().getHref(), PurchaseOrderDTO.class);

            }else if(phr.getSupplier().equals("RentITT9")){
                ExtensionDTO_Team9 et9 = new ExtensionDTO_Team9();
                et9.setEndDate(plantHireRequestDTO.getRentalPeriod().getEndDate());
                RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
                template.patchForObject(phr.getPo().get_xlink().getHref() + "/extend", et9, Object.class);
                purchaseOrderDTOT9 = restTemplate.getForObject(phr.getPo().get_xlink().getHref(), RentITPODto_Team9.class);
                System.out.println("XXXXXXX   " +purchaseOrderDTOT9);
                purchaseOrderDTO.setId(purchaseOrderDTOT9.get_id());
                purchaseOrderDTO.setCustomerCompany(purchaseOrderDTOT9.getCustomerCompany());
                purchaseOrderDTO.setStatus(purchaseOrderDTOT9.getStatus());
                purchaseOrderDTO.setRentalPeriod(purchaseOrderDTOT9.getRentalPeriod());
                purchaseOrderDTO.setPlantEntry(purchaseOrderDTOT9.getPlant());
                purchaseOrderDTO.setTotal(purchaseOrderDTOT9.getTotal());

            }
        }
        catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
            if (HttpStatus.BAD_REQUEST.equals(httpClientOrServerExc.getStatusCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The Extension request is rejected!");
            }
        }

        System.out.println("last po"+ purchaseOrderDTO);


        return purchaseOrderDTO;


    }

}