package com.example.demo.sales.domain.model;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(force=true,access= AccessLevel.PUBLIC)
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany
    List<PlantReservation> reservations;

    @ManyToOne
    PlantInventoryEntry plantEntry;

    @ManyToOne
    Invoice invoice;

    LocalDate issueDate;
    LocalDate paymentSchedule;
    @Column(precision=8,scale=2)
    BigDecimal total;

    @Enumerated(EnumType.STRING)
    POStatus status;

    @Embedded
    BusinessPeriod rentalPeriod;

    Boolean isCancelledDueToMaintenance = false;
    String contactEmail;

    String customerCompany;
//    String contactPerson;
//    String constructionSiteAddress;

    public static PurchaseOrder of(PlantInventoryEntry entry, BusinessPeriod period, PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder po = new PurchaseOrder();
        po.reservations = new ArrayList<>();
        po.plantEntry = entry;
        po.issueDate = LocalDate.now();
        po.paymentSchedule = LocalDate.now().plusDays(30);
        po.total = purchaseOrderDTO.getTotal();
        po.status = POStatus.PENDING;
        po.rentalPeriod = period;
        po.customerCompany = purchaseOrderDTO.getCustomerCompany();
        return po;
    }

    public static PurchaseOrder unavailableRejection(PlantInventoryEntry entry, BusinessPeriod period, PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder poReject = new PurchaseOrder();
        poReject.reservations = new ArrayList<>();
        poReject.plantEntry = entry;
        poReject.issueDate = LocalDate.now();
        poReject.paymentSchedule = LocalDate.now().plusDays(30);
        poReject.total = purchaseOrderDTO.getTotal();
        poReject.status = purchaseOrderDTO.getStatus();
        poReject.rentalPeriod = period;
        return poReject;
    }

}
