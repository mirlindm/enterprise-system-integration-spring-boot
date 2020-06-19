package com.buildit.payables.rest;

import com.buildit.payables.application.dto.InvoiceDTO;
import com.buildit.payables.application.dto.PaymentDTO;
import com.buildit.payables.application.services.PayablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payables")
public class PayablesRestController {

    @Autowired
    PayablesService payablesService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/invoices/{id}")
    public InvoiceDTO getInvoice(@PathVariable("id") Long id){
        return payablesService.getInvoice(id);
    }


    // Requirement CC9 and CC10
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/invoices")
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody InvoiceDTO invoiceDTO) throws Exception{
        InvoiceDTO invDTO = payablesService.createInvoice(invoiceDTO);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<InvoiceDTO>(invDTO, headers, HttpStatus.CREATED);
    }

    // Requirement CC11 - Approve Invoice
    @PreAuthorize("hasAnyRole('WORKS','ADMIN')")
    @PutMapping("/invoices/{id}")
    public ResponseEntity<InvoiceDTO> approveInvoice(@PathVariable("id") Long id) throws Exception{
        InvoiceDTO invDTO = payablesService.approveInvoice(id);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<InvoiceDTO>(invDTO, headers, HttpStatus.CREATED);
    }

    // Requirement CC12 - Reject Invoice
    @PreAuthorize("hasAnyRole('WORKS','ADMIN')")
    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<InvoiceDTO> rejectInvoice(@PathVariable("id") Long id) throws Exception{
        InvoiceDTO invDTO = payablesService.rejectInvoice(id);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<InvoiceDTO>(invDTO, headers, HttpStatus.CREATED);
    }

    // Requirement CC12 - Create/Submit Remittance Advice
    @PreAuthorize("hasAnyRole('WORKS','ADMIN')")
    @PostMapping("/invoices/{id}/pay")
    public ResponseEntity<PaymentDTO> createPayment(@PathVariable Long id, @RequestBody PaymentDTO paymentDTO) throws Exception{
        PaymentDTO payDTO = payablesService.createPayment(id,paymentDTO);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<PaymentDTO>(payDTO, headers, HttpStatus.CREATED);
    }



}
