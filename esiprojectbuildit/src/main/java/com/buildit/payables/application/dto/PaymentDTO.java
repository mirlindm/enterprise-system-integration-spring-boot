package com.buildit.payables.application.dto;

import com.buildit.payables.domain.model.Invoice;
import com.buildit.payables.domain.model.Payment;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentDTO extends RepresentationModel<PaymentDTO> {
    private Long id;
    private LocalDate paymentDate;
    private String bankName;
    private BigDecimal amountPaid;
    private InvoiceDTO invoice;

    public Payment toPayment(Invoice invoice, PaymentDTO paymentDTO){
        return Payment.of(invoice , paymentDTO);
    }
}
