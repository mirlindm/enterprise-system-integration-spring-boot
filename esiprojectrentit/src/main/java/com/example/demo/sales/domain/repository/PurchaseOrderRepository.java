package com.example.demo.sales.domain.repository;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.sales.domain.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Transactional
    @Modifying
    @Query(value="update purchase_order set end_date = ?2, total = ?3 where id = ?1", nativeQuery=true)
    Integer extendPO(Long poId, LocalDate endDate, BigDecimal total);

    @Query(value="select * from purchase_order where CUSTOMER_COMPANY = ?1", nativeQuery=true)
    List<PurchaseOrder> getSubmittedPOs(String customerCompany);


}
