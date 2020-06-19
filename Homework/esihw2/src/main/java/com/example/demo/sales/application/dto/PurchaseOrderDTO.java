package com.example.demo.sales.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.sales.domain.model.POStatus;
import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

@Data
public class PurchaseOrderDTO extends ResourceSupport {
    Long _id;
    BusinessPeriodDTO rentalPeriod;
    PlantInventoryEntryDTO plant;
    POStatus status;


    public BusinessPeriodDTO getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(BusinessPeriodDTO rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }



    public PlantInventoryEntryDTO getPlant() {
        return plant;
    }

    public void setPlant(PlantInventoryEntryDTO plant) {
        this.plant = plant;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public POStatus getStatus() {
        return status;
    }

    public void setStatus(POStatus status) {
        this.status = status;
    }
}
