package com.buildit.integration;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PlantAvailabilityRequestDTO {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}