package com.buildit.payables.domain.repositories;

import com.buildit.payables.domain.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value="select * from invoice i WHERE i.po_id = ?1 LIMIT 1", nativeQuery=true)
    Optional<Invoice> getInvoiceByPoId(Long po_id);
}
