package com.buildit.payables.domain.model;

import com.buildit.payables.application.dto.PaymentDTO;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate paymentDate;

    private String bankName;

    private BigDecimal amountPaid;

    @OneToOne
    private Invoice invoice;


    public static Payment of(Invoice invoice, PaymentDTO paymentDTO){
        Payment payment = new Payment();
        payment.paymentDate = LocalDate.now();
        payment.bankName = paymentDTO.getBankName();
        payment.amountPaid = paymentDTO.getAmountPaid();
        payment.invoice = invoice;
        return payment;
    }
}
