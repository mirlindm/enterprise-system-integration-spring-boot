package com.buildit.maintenance.domain.model;

import com.buildit.common.rest.ExtendedLink;
import com.buildit.maintenance.application.dto.MaintenanceRequestDTO;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(force=true,access= AccessLevel.PROTECTED)
public class PlantInventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private ExtendedLink _xlink;

    public static PlantInventoryItem of(MaintenanceRequestDTO maintenanceRequestDTO, String GET_PLANT_URI) {
        PlantInventoryItem pii = new PlantInventoryItem();
        pii._xlink = new ExtendedLink(GET_PLANT_URI + maintenanceRequestDTO.getPlantID(),"getPlantItem", HttpMethod.GET);
        return pii;
    }

}
