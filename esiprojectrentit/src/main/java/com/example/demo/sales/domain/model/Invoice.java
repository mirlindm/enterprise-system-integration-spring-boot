package com.example.demo.sales.domain.model;

import com.example.demo.common.rest.ExtendedLink;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor(force=true,access= AccessLevel.PUBLIC)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    PurchaseOrder purchaseOrder;

    LocalDate dueDate;
    Boolean isPaid = false;

    @Lob
    private ExtendedLink _xlink;

}
