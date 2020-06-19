package com.example.demo.sales.domain.model;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(force=true,access= AccessLevel.PROTECTED)
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany
    List<PlantReservation> reservations;

    @ManyToOne
    PlantInventoryEntry plant;

    LocalDate issueDate;
    LocalDate paymentSchedule;
    @Column(precision=8,scale=2)
    BigDecimal total;

    @Enumerated(EnumType.STRING)
    POStatus status;

    @Embedded
    BusinessPeriod rentalPeriod;

    public static PurchaseOrder of(PlantInventoryEntry entry, BusinessPeriod period) {
        PurchaseOrder po = new PurchaseOrder();
        po.plant = entry;
        po.rentalPeriod = period;
        po.reservations = new ArrayList<>();
        po.issueDate = LocalDate.now();
        po.status = POStatus.PENDING;
        return po;
    }

}
