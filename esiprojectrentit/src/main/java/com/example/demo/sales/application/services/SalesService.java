package com.example.demo.sales.application.services;

import com.example.demo.common.application.ErrorResponseHelper;
import com.example.demo.common.application.MailHelper;
import com.example.demo.common.application.dto.*;
import com.example.demo.common.application.exception.PlantNotFoundException;
import com.example.demo.common.rest.ExtendedLink;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.dto.PlantReservationDTO;
import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.inventory.application.service.PlantInventoryEntryAssembler;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.sales.application.dto.InvoiceDTO;
import com.example.demo.sales.application.dto.InvoiceResponseDTO;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.Invoice;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.InvoiceRepository;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SalesService {

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PlantReservationRepository plantReservationRepository;

    @Autowired
    PlantInventoryItemRepository itemRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    @Autowired
    PlantInventoryEntryAssembler entryAssembler;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    RestTemplate restTemplate;


    //Requirement PS4
    public PurchaseOrderDTO createPO (PurchaseOrderDTO poDto) throws Exception {
        Map<String, List<String>> allErrors = new HashMap<>();
        BusinessPeriod period = BusinessPeriod.of(poDto.getRentalPeriod().getStartDate(), poDto.getRentalPeriod().getEndDate());

        //Validate Business Period
        DataBinder binder = new DataBinder(period);
        binder.addValidators(new BusinessPeriodValidator());
        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            Map<String, List<String>> itemErrors = ErrorResponseHelper.objectErrorsToMap(binder.getBindingResult().getAllErrors());
            allErrors.putAll(itemErrors);
            throw new Exception(ErrorResponseHelper.errorMapToJsonString(allErrors));
        }

        //Validate Purchase Order field
        DataBinder poBinder = new DataBinder(poDto);
        poBinder.addValidators(new PurchaseOrderCreationValidator());
        poBinder.validate();

        if (poBinder.getBindingResult().hasErrors()) {
            Map<String, List<String>> itemErrors = ErrorResponseHelper.objectErrorsToMap(poBinder.getBindingResult().getAllErrors());
            allErrors.putAll(itemErrors);
            throw new Exception(ErrorResponseHelper.errorMapToJsonString(allErrors));
        }


        // Return all errors together
        if(!allErrors.isEmpty()){
            throw new Exception(ErrorResponseHelper.errorMapToJsonString(allErrors));
        }


        //Check if Plant Inventory Entry exists
        PlantInventoryEntry plantEntry = inventoryService.findEntryById(poDto.getPlantEntry().get_id());

        if(plantEntry == null) {
            throw new Exception("Plant Entry Not Found");
        }

        //Convert plant inventory entry to DTO using assembler
        //PlantInventoryEntryDTO entryDto= entryAssembler.toModel(plantEntry);

        //Check if plant item exists during the given period of po creation
        Iterator<PlantInventoryItemDTO> availableItems = inventoryService.findAvailableItems(
                plantEntry.getName(),
                poDto.getRentalPeriod().getStartDate(),
                poDto.getRentalPeriod().getEndDate()).iterator();

        if(!availableItems.hasNext()) {
            PurchaseOrder pOrder = poDto.rejectUnavailablePlantItemPO(plantEntry,period);
            purchaseOrderRepository.save(pOrder);
            throw new Exception("No available items");
        }

        //Calculate total price
        BigDecimal calculatedPrice = calculatePrice(plantEntry, period);

        PurchaseOrder pOrder = poDto.toPurchaseOrder(plantEntry,period,calculatedPrice, poDto.getCustomerCompany());
        purchaseOrderRepository.save(pOrder);

        return purchaseOrderAssembler.toModel(pOrder);

    }


    public PurchaseOrderDTO findPO(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElse(null);
        return purchaseOrderAssembler.toModel(po);
    }

    //Requirement PS4
    public PurchaseOrderDTO rejectPO(Long id) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElse(null);
        while (!po.getReservations().isEmpty())
            plantReservationRepository.delete(po.getReservations().remove(0));
//        for(PlantReservation pr : po.getReservations()){
//            plantReservationRepository.delete(pr);
//        }
        po.setStatus(POStatus.REJECTED);
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toModel(po);
    }

    //Requirement PS4
    public PurchaseOrderDTO acceptPO(Long id) throws Exception {
        PurchaseOrder po =purchaseOrderRepository.findById(id).orElse(null);
        PlantInventoryEntry pEntry = inventoryService.findEntryById(po.getPlantEntry().getId());

        Iterator<PlantInventoryItemDTO> availableItems = inventoryService.findAvailableItems(
                pEntry.getName(),
                po.getRentalPeriod().getStartDate(),
                po.getRentalPeriod().getEndDate()).iterator();

        if(!availableItems.hasNext()) {
            throw new Exception("Item is no longer available");
        }

        validatePO(po);
        po.setStatus(POStatus.ACCEPTED);

        //Create Plant Reservation
        PlantInventoryItem item = inventoryService.findItemById(availableItems.next().get_id());

        PlantReservationDTO prDto = new PlantReservationDTO();

        PlantReservation reservation = prDto.toPlantReservation(item, po.getRentalPeriod());
        reservation.setRental(po);
        plantReservationRepository.save(reservation);
        po.getReservations().add(reservation);
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toModel(po);
    }

    //Requirement PS7
    public PurchaseOrderDTO cancelPO(Long id) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElse(null);
        List<POStatus> allowedStatus = Arrays.asList(POStatus.PENDING, POStatus.ACCEPTED, POStatus.REJECTED);

        if(!allowedStatus.contains(po.getStatus())){
            throw new Exception("Cancellation not allowed");
        }

        po.setStatus(POStatus.CANCELLED);
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toModel(po);
    }

    //Requirement PS6
    public PurchaseOrderDTO extendPO (PurchaseOrderDTO poDto, Long id) throws  Exception{

        //Check if PO exists
        PurchaseOrder po = purchaseOrderRepository.findById(id).orElse(null);


        //Validate Business Period
        Map<String, List<String>> allErrors = new HashMap<>();
        BusinessPeriod period = BusinessPeriod.of(po.getRentalPeriod().getStartDate(), poDto.getRentalPeriod().getEndDate());

        //Validate Business Period
        DataBinder binder = new DataBinder(period);
        binder.addValidators(new BusinessPeriodValidator());
        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            Map<String, List<String>> itemErrors = ErrorResponseHelper.objectErrorsToMap(binder.getBindingResult().getAllErrors());
            allErrors.putAll(itemErrors);
            throw new Exception(ErrorResponseHelper.errorMapToJsonString(allErrors));
        }

        // Return all errors together
        if(!allErrors.isEmpty()){
            throw new Exception(ErrorResponseHelper.errorMapToJsonString(allErrors));
        }

        //Check if PO is accepted
        if(po.getStatus() == POStatus.ACCEPTED || po.getStatus() == POStatus.PENDING) {

//            //Check if plant item exists during the given period of po creation
//            String plantAvailability = inventoryService.checkAvailabilityOfEntry(po.getPlantEntry().getId(), po.getRentalPeriod().getEndDate().plusDays(1).toString(), poDto.getRentalPeriod().getEndDate().toString());


            PlantReservation reservationByPO =  plantReservationRepository.findReservationsByRentalId(po.getId());

            PlantInventoryItem item = itemRepository.findById(reservationByPO.getPlant().getId()).orElse(null);

            List<PlantReservation> reservationsByPlantId = plantReservationRepository.findAllReservationsByPlantId(item.getId());

            if(item != null) {
                Boolean canExtend  = true;

                BusinessPeriod extendedPeriod = BusinessPeriod.of(po.getRentalPeriod().getEndDate().plusDays(1), poDto.getRentalPeriod().getEndDate());

                for(PlantReservation reservation : reservationsByPlantId){
                    if(reservation.getSchedule().overlapsWith(extendedPeriod)){
                        canExtend=false;
                        break;
                    }
                }

                if(canExtend){
                    //Calculate total price
                    PlantInventoryEntry plantEntry = inventoryService.findEntryById(po.getPlantEntry().getId());
                    BigDecimal calculatedPrice = calculatePrice(plantEntry, period);
                    po.setTotal(calculatedPrice);
                    po.setRentalPeriod(period);
                    purchaseOrderRepository.save(po);

                    //Modify dates in plant reservation
                    reservationByPO.setSchedule(period);
                    plantReservationRepository.save(reservationByPO);
                }else{
                throw new Exception("Your PO request cannot be extended");
                }
            }
        }
        return purchaseOrderAssembler.toModel(po);
    }

    //Requirement PS8
    public PurchaseOrderDTO plantDispatched(Long po_id) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(po_id).orElse(null);

        if(po.getStatus() != POStatus.ACCEPTED){
            throw new Exception("Current status of purchase order does not allow dispatching");
        }

        po.setStatus(POStatus.PLANT_DISPATCHED);
        purchaseOrderRepository.save(po);
        System.out.println(purchaseOrderAssembler.toModel(po).toString());
        return purchaseOrderAssembler.toModel(po);
    }

    //Requirement PS9
    public PurchaseOrderDTO plantDelivered(Long po_id) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(po_id).orElse(null);

        if(po.getStatus() != POStatus.PLANT_DISPATCHED){
            throw new Exception("Current status of purchase order does not allow marking it as 'DELIVERED'");
        }

        po.setStatus(POStatus.PLANT_DELIVERED);
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toModel(po);
    }

    //Requirement PS9
    public PurchaseOrderDTO plantRejected(Long po_id) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(po_id).orElse(null);

        if(po.getStatus() != POStatus.PLANT_DISPATCHED){
            throw new Exception("Current status of purchase order does not allow marking it as 'REJECTED'");
        }
        System.out.println("===> REJECTED");

        po.setStatus(POStatus.PLANT_REJECTED);
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toModel(po);
    }

    //Requirement PS13
    public PurchaseOrderDTO plantReturned(Long po_id) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(po_id).orElse(null);

        if(po.getStatus() != POStatus.PLANT_DELIVERED){
            throw new Exception("Current status of purchase order does not allow marking it as 'RETURNED'");
        }


        if(po.getContactEmail() != null){
            String mailBody = "Your order for item \""+po.getPlantEntry().getName()+"\" is cancelled";

            MailHelper.sendmail(mailBody,
                    po.getContactEmail()
            );
        }
        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setPurchaseOrder(po);
        invoice = invoiceRepository.save(invoice);

        po.setStatus(POStatus.PLANT_RETURNED);
        po.setInvoice(invoice);
        purchaseOrderRepository.save(po);

        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceDate(LocalDate.now());
        invoiceDTO.setInvoiceNumber(invoice.getId().toString());
        invoiceDTO.setSupplier("RENTIT");
        invoiceDTO.setTotal(po.getTotal());
        invoiceDTO.setPoID(po.getId());

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("user1");
        authDTO.setPassword("password1");
        TokenDTO tokenDTO = restTemplate.postForObject("http://localhost:8080/token/signin",authDTO,TokenDTO.class);
        System.out.println("XXXXXXXXXXXXXXXXX"+ tokenDTO.getToken());

        // Integration with BuildIT
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+tokenDTO.getToken());
        HttpEntity<InvoiceDTO> entity = new HttpEntity<InvoiceDTO>(invoiceDTO,headers);
        InvoiceResponseDTO invoiceResponseDTO = restTemplate.postForObject("http://localhost:8080/api/payables/invoices", entity, InvoiceResponseDTO.class);

        System.out.println(invoiceResponseDTO.get_links().get(0).getHref());
        
        System.out.println(invoiceRepository.findAll().size());

        invoice.set_xlink(new ExtendedLink(invoiceResponseDTO.get_links().get(0).getHref(), "GetInvoice", HttpMethod.GET));
        invoiceRepository.save(invoice);


        return purchaseOrderAssembler.toModel(po);
    }

    // Requirement PS15
    public String addRemittance(Long po_id) throws Exception {
        System.out.println("service");
        System.out.println("total po count: "+purchaseOrderRepository.findAll().size());
        PurchaseOrder po = purchaseOrderRepository.findById(po_id).orElse(null);
        System.out.println("current po id: "+po.getId());


        if(po.getStatus() != POStatus.PLANT_RETURNED){
            throw new Exception("Plant has not been returned yet");
        }else if(po.getInvoice() == null){
            throw new Exception("No invoice yet for this purchase order");
        }

        Invoice invoice = po.getInvoice();
        System.out.println("no error / invoice id: "+invoice.getId());

        invoice.setIsPaid(true);
        invoiceRepository.save(invoice);

        po.setStatus(POStatus.PAID);
        purchaseOrderRepository.save(po);

        System.out.println("gonna return");
        return "REMITTANCE ADVICE RECEIVED";
    }


    // PS5
    public List<PurchaseOrderDTO> findSubmittedPOs(String custName){
        List<PurchaseOrder> POs = purchaseOrderRepository.getSubmittedPOs(custName);
        ArrayList<PurchaseOrderDTO> PODTOs = new ArrayList<>();

        for(PurchaseOrder i : POs){
            PODTOs.add(purchaseOrderAssembler.toModel(i));
        }
        return PODTOs;
    }

    private void validatePO(PurchaseOrder po) throws Exception {
        if(po == null)
            throw new Exception("PO Not Found");
        if(po.getStatus() != POStatus.PENDING)
            throw new Exception("PO cannot be accepted as it is not Pending");
    }


    public BigDecimal calculatePrice(PlantInventoryEntry plantInventoryEntry, BusinessPeriod rentalPeriod) {
        BigDecimal entryPrice = plantInventoryEntry.getPrice();
        LocalDate startDate = rentalPeriod.getStartDate();
        LocalDate endDate = rentalPeriod.getEndDate();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return entryPrice.multiply(new BigDecimal(days));
    }

}
