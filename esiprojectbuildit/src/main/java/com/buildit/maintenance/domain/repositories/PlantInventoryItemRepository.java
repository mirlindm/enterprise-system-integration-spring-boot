package com.buildit.maintenance.domain.repositories;

import com.buildit.maintenance.domain.model.PlantInventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantInventoryItemRepository extends JpaRepository<PlantInventoryItem, Long> {

}
