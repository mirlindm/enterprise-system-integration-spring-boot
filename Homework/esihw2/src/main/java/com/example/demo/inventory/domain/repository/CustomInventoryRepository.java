package com.example.demo.inventory.domain.repository;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;

import java.time.LocalDate;
import java.util.List;

public interface CustomInventoryRepository {
    List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate);
}
