package com.buildit.procurement.domain.repositories;

import com.buildit.rental.domain.model.PlantInventoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantInventoryEntryRepository extends JpaRepository<PlantInventoryEntry, Long> {
}
