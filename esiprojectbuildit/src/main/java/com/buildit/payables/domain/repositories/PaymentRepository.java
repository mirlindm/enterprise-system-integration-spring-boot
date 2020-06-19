package com.buildit.payables.domain.repositories;

import com.buildit.payables.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
