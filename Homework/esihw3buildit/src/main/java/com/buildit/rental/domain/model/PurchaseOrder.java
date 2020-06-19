package com.buildit.rental.domain.model;

import com.buildit.common.rest.ExtendedLink;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private ExtendedLink _xlink;
}
