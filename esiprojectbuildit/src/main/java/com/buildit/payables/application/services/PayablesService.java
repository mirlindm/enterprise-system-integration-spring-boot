package com.buildit.payables.application.services;

import com.buildit.payables.application.dto.InvoiceDTO;
import com.buildit.payables.application.dto.PaymentDTO;
import com.buildit.payables.domain.model.InvStatus;
import com.buildit.payables.domain.model.Invoice;
import com.buildit.payables.domain.model.Payment;
import com.buildit.payables.domain.repositories.InvoiceRepository;
import com.buildit.payables.domain.repositories.PaymentRepository;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.domain.model.PurchaseOrder;
import com.buildit.rental.domain.repositories.PurchaseOrderRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PayablesService {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceAssembler invoiceAssembler;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PaymentAssembler paymentAssembler;

    @Autowired
    RentalService rentalService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PlantHireRequestRepository plantHireRequestRepository;


    public InvoiceDTO getInvoice(Long id){
        Invoice invoice = invoiceRepository.findById(id).orElse(null);
        return invoiceAssembler.toModel(invoice);
    }

    // Requirement CC9
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.getPoByReferenceId(invoiceDTO.getPoID()).orElse(null);
        if(po == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The PO is Invalid");
        }
        if(purchaseOrderRepository.getPoPayments(po.getId()) > 0.0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The PO is already paid!");
        }

        Invoice inv = invoiceRepository.getInvoiceByPoId(po.getId()).orElse(null);
        if(inv != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is already submitted invoice for this PO!");
        }

        PlantHireRequest plantHireRequest = plantHireRequestRepository.getPlantHireRequestByPo(po.getId()).orElse(null);

        if(plantHireRequest.getStatus()== PHRStatus.ACCEPTED){
            Invoice invoice = invoiceDTO.toInvoice(po, invoiceDTO, InvStatus.VALIDATED);
            invoice = invoiceRepository.save(invoice);
            return invoiceAssembler.toModel(invoice);
        }
        else{
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "The PlantHireRequest is Invalid!");
        }

    }


    // Requirement CC11 - Approve Invoice
    public InvoiceDTO approveInvoice(Long id) throws Exception {
        Invoice invoice = invoiceRepository.findById(id).orElse(null);
        if (invoice.getStatus().equals(InvStatus.VALIDATED)) {
            //if(invoice.getStatus().equals(InvStatus.APPROVED)){
             //   throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invoice is already approved!");
            //}
            invoice.setStatus(InvStatus.APPROVED);
            invoiceRepository.save(invoice);
            PlantHireRequest hr = plantHireRequestRepository.getPlantHireRequestByPo(invoice.getPo().getId()).orElse(null);
            hr.setStatus(PHRStatus.COMPLETED);
            plantHireRequestRepository.save(hr);
            return invoiceAssembler.toModel(invoice);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The invoice can't be approved in this stage!");

        }


    }

    // Requirement CC11 - Reject Invoice
    public InvoiceDTO rejectInvoice(Long id) throws Exception {
        Invoice invoice = invoiceRepository.findById(id).orElse(null);
        if (invoice.getStatus().equals(InvStatus.VALIDATED)) {
            invoice.setStatus(InvStatus.REJECTED);
            invoiceRepository.save(invoice);
            return invoiceAssembler.toModel(invoice);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The invoice can't be rejected in this stage!");
        }
    }

    // CC12
    public PaymentDTO createPayment(Long invoiceID, PaymentDTO paymentDTO) throws Exception {

        Invoice invoice = invoiceRepository.findById(invoiceID).orElse(null);
        if (invoice.getStatus().equals(InvStatus.APPROVED)) {
            Payment payment = paymentDTO.toPayment(invoice, paymentDTO);
            // Extra code needed to submit in RentIT
            RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
            String out = template.patchForObject("http://localhost:8090/api/sales/po/"+ invoice.getPo().getReferenceID().toString() +"/remittance", "request", String.class);

            if (out.contains("REMITTANCE ADVICE RECEIVED")) {
                payment = paymentRepository.save(payment);
                invoice.setStatus(InvStatus.PAID);
                invoiceRepository.save(invoice);
                return paymentAssembler.toModel(payment);
            }
            else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Problem occurred in submitting the remittance advice!");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The invoice is not yet approved, it can't be paid!");
        }

    }




}
