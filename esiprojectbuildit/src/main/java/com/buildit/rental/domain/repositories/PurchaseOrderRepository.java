package com.buildit.rental.domain.repositories;

import com.buildit.rental.domain.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    @Query(value="select * from purchase_order po WHERE po.REFERENCEID = ?1 LIMIT 1", nativeQuery=true)
    Optional<PurchaseOrder> getPoByReferenceId(Long reference_id);

    @Query(value="select sum(nvl(AMOUNT_PAID,0)) from purchase_order po LEFT JOIN invoice i on po.id = i.po_id LEFT JOIN payment p on p.invoice_id = i.id WHERE po.id = ?1 ", nativeQuery=true)
    Float getPoPayments(Long poID);
}
