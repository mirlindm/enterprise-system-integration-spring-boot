package com.example.demo.inventory.domain.repository;

import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantInventoryItemRepository extends JpaRepository<PlantInventoryItem, Long> {
    PlantInventoryItem findOneByPlantInfo(PlantInventoryEntry entry);
    List<PlantInventoryItem> findAllByPlantInfo(PlantInventoryEntry entry);

    @Query("select p from PlantInventoryItem p where p.equipmentCondition='SERVICEABLE'")
    List<PlantInventoryItem> findAllServiceable();

    @Query(value="select * from plant_inventory_item where equipment_condition='SERVICEABLE' AND plant_info_id = ?1", nativeQuery=true)
    List<PlantInventoryItem> findAllServiceableByEntryId(Long entryId);

}
