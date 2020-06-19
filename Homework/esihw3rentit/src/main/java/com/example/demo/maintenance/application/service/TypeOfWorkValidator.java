package com.example.demo.maintenance.application.service;

import com.example.demo.maintenance.application.dto.TowEqCondition;
import org.springframework.stereotype.Service;
import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.maintenance.domain.model.TypeOfWork;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Service
public class TypeOfWorkValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) { return TowEqCondition.class.isAssignableFrom(aClass); }

    @Override
    public void validate(Object o, Errors errors) {

        TowEqCondition tt = (TowEqCondition) o;
        TypeOfWork tow = tt.getTypeOfWork();
        EquipmentCondition eq = tt.getEquipmentCondition();

        if(tow == TypeOfWork.PREVENTIVE && eq != EquipmentCondition.SERVICEABLE){
            String fieldName = "typeOfWork";
            String errorMessage = "A PREVENTIVE maintenance task only can be scheduled for SERVICEABLE plants";
            errors.rejectValue(fieldName, errorMessage);
        }else if(tow == TypeOfWork.CORRECTIVE && (eq != EquipmentCondition.UNSERVICEABLEREPAIRABLE && eq != EquipmentCondition.UNSERVICEABLEINCOMPLETE)){
            String fieldName = "typeOfWork";
            String errorMessage = "CORRECTIVE maintenance tasks only can be scheduled for UNSERVICEABLE REPAIRABLE and INCOMPLETE plants";
            errors.rejectValue(fieldName, errorMessage);
        }else if(tow == TypeOfWork.OPERATIVE && eq == EquipmentCondition.UNSERVICEABLECONDEMNED){
            String fieldName = "typeOfWork";
            String errorMessage = "OPERATIONAL maintenance tasks can be scheduled for ANY plant, except for the UNSERVICEABLE CONDEMNED ones";
            errors.rejectValue(fieldName, errorMessage);
        }

    }
}
