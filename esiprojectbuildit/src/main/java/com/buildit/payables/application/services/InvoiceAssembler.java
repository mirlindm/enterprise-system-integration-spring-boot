package com.buildit.payables.application.services;

import com.buildit.payables.application.dto.InvoiceDTO;
import com.buildit.payables.domain.model.Invoice;
import com.buildit.payables.rest.PayablesRestController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class InvoiceAssembler extends RepresentationModelAssemblerSupport<Invoice, InvoiceDTO> {

    public InvoiceAssembler(){super(PayablesRestController.class, InvoiceDTO.class);}

    @Override
    public InvoiceDTO toModel(Invoice invoice){
        InvoiceDTO invoiceDTO = createModelWithId(invoice.getId(), invoice);
        invoiceDTO.setInvoiceDate(invoice.getInvoiceDate());
        invoiceDTO.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceDTO.set_id(invoice.getId());
        invoiceDTO.setPoID(invoice.getPo().getId());
        invoiceDTO.setSupplier(invoice.getSupplier());
        invoiceDTO.setTotal(invoice.getTotal());
        invoiceDTO.setStatus(invoice.getStatus());

        return invoiceDTO;
    }


}
