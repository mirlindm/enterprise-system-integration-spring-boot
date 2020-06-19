package com.example.demo.inventory.domain.repository;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantInventoryItemRepository extends JpaRepository<PlantInventoryItem, Long> {
    PlantInventoryItem findOneByPlantInfo(PlantInventoryEntry entry);
    List<PlantInventoryItem> findAllByPlantInfo(PlantInventoryEntry entry);

}
