package com.example.demo.common.application.dto;

import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class PlantInventoryItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return PlantInventoryItem.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PlantInventoryItem item = (PlantInventoryItem) o;

        if(item.equals(new PlantInventoryItem())){
            errors.rejectValue("id", "Specified plant item does not exist");
        };
    }
}
