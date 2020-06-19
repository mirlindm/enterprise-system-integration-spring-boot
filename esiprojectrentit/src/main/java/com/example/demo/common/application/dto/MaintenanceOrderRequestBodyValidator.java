package com.example.demo.common.application.dto;

import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.maintenance.application.dto.MaintenanceOrderRequestDTO;
import com.example.demo.maintenance.domain.model.MaintenanceTaskRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class MaintenanceOrderRequestBodyValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return MaintenanceOrderRequestDTO.class.equals(aClass);
    }
    @Override
    public void validate(Object o, Errors errors) {
        MaintenanceOrderRequestDTO req = (MaintenanceOrderRequestDTO) o;
        BusinessPeriod period = BusinessPeriod.of(req.getExpectedPeriod().getStartDate(), req.getExpectedPeriod().getEndDate());;

        if(period.getStartDate() == null)
            errors.rejectValue("expectedPeriod.startDate", "startDate cannot be null");
        if(period.getEndDate() == null)
            errors.rejectValue("expectedPeriod.endDate", "endDate cannot be null");

        if (period.getStartDate() != null && period.getEndDate() != null) {
            if(!period.getStartDate().isBefore(period.getEndDate()))
                errors.rejectValue("expectedPeriod.startDate", "startDate must be before endDate");
            if(period.getStartDate().isBefore(LocalDate.now()))
                errors.rejectValue("expectedPeriod.startDate", "startDate must be in the future");
            if(period.getEndDate().isBefore(LocalDate.now()))
                errors.rejectValue("expectedPeriod.endDate", "endDate must be in the future");
        }

        if(req.getConstructionSiteId() == null)
            errors.rejectValue("constructionSiteId", "Construction site id cannot  be null");

        if(req.getDescription() == null)
            errors.rejectValue("description", "Description cannot  be null");

        if(req.getSiteEngineerName() == null)
            errors.rejectValue("siteEngineerName", "Site engineer name cannot  be null");
    }
}

