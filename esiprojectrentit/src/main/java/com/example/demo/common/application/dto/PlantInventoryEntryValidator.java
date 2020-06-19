package com.example.demo.common.application.dto;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
public class PlantInventoryEntryValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return PlantInventoryEntry.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PlantInventoryEntry entry = (PlantInventoryEntry) o;


        if(entry == null){
            errors.rejectValue("id", "Specified plant entry does not exist");
        };
    }
}

