package com.example.demo.common.application.dto;

import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class PurchaseOrderCreationValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return PurchaseOrderDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PurchaseOrderDTO poDTO = (PurchaseOrderDTO) o;

        if(poDTO.getPlantEntry().get_id() == null){
            errors.rejectValue("id", "Invalid Plant Entry");
        };
    }
}

