package com.buildit.maintenance.domain.model;

import com.buildit.common.rest.ExtendedLink;
import com.buildit.maintenance.application.dto.MaintenanceRequestDTO;
import lombok.Data;
import org.springframework.http.HttpMethod;

import javax.persistence.*;

@Entity
@Data
public class MaintenanceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private ExtendedLink _xlink;

    public static MaintenanceOrder of(Long id, String GET_MO_URI) {
        MaintenanceOrder mo = new MaintenanceOrder();
        mo._xlink = new ExtendedLink(GET_MO_URI + "/orders" + id,"getMo", HttpMethod.GET);
        return mo;
    }

}
