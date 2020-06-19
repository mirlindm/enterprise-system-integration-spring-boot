package com.example.demo.common.application.dto;

import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.MaintenanceTaskRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class RequestBodyValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return MaintenanceTaskRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MaintenanceTaskRequest req = (MaintenanceTaskRequest) o;
        BusinessPeriod period = BusinessPeriod.of(req.getRentalPeriod().getStartDate(), req.getRentalPeriod().getEndDate());;
        BigDecimal totalAmount = req.getTotal();

        if(totalAmount.compareTo(BigDecimal.ZERO) < 0)
            errors.rejectValue("total", "Total amount cannot be negative");

        if(period.getStartDate() == null)
            errors.rejectValue("rentalPeriod.startDate", "startDate cannot be null");
        if(period.getEndDate() == null)
            errors.rejectValue("rentalPeriod.endDate", "endDate cannot be null");

        if (period.getStartDate() != null && period.getEndDate() != null) {
            if(!period.getStartDate().isBefore(period.getEndDate()))
                errors.rejectValue("rentalPeriod.startDate", "startDate must be before endDate");
            if(period.getStartDate().isBefore(LocalDate.now()))
                errors.rejectValue("rentalPeriod.startDate", "startDate must be in the future");
            if(period.getEndDate().isBefore(LocalDate.now()))
                errors.rejectValue("rentalPeriod.endDate", "endDate must be in the future");
        }
    }
}
