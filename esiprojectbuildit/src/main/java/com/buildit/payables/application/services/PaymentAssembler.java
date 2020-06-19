package com.buildit.payables.application.services;

import com.buildit.payables.application.dto.InvoiceDTO;

import com.buildit.payables.application.dto.PaymentDTO;
import com.buildit.payables.domain.model.Payment;
import org.springframework.hateoas.RepresentationModel;
import com.buildit.payables.rest.PayablesRestController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class PaymentAssembler extends RepresentationModelAssemblerSupport<Payment, PaymentDTO> {

    public PaymentAssembler(){super(PayablesRestController.class, PaymentDTO.class);}

    @Override
    public PaymentDTO toModel(Payment payment){
        PaymentDTO paymentDTO = createModelWithId(payment.getId(), payment);

        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.set_id(payment.getInvoice().getId());
        invoiceDTO.setTotal(payment.getInvoice().getTotal());
        invoiceDTO.setSupplier(payment.getInvoice().getSupplier());
        invoiceDTO.setPoID(payment.getInvoice().getPo().getId());
        invoiceDTO.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
        invoiceDTO.setInvoiceDate(payment.getInvoice().getInvoiceDate());
        invoiceDTO.setStatus(payment.getInvoice().getStatus());
        paymentDTO.setAmountPaid(payment.getAmountPaid());
        paymentDTO.setBankName(payment.getBankName());
        paymentDTO.setId(payment.getId());
        paymentDTO.setInvoice(invoiceDTO);
        paymentDTO.setPaymentDate(payment.getPaymentDate());
        //Set the status of invoice

        return paymentDTO;
    }
}