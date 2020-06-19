package com.buildit.maintenance.domain.model;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.common.rest.ExtendedLink;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import lombok.Data;
import org.springframework.http.HttpMethod;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class MaintenanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private BusinessPeriod expectedPeriod;

    @Enumerated(EnumType.STRING)
    private MRStatus status;

    private String siteEngineerName;

    private Long constructionSiteId;

    private String description;

    private LocalDate issueDate;

    @OneToOne
    private PlantInventoryItem plant;

    @OneToOne
    private MaintenanceOrder mo;
    
}
