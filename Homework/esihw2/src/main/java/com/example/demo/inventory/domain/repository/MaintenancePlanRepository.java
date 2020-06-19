package com.example.demo.inventory.domain.repository;

import com.example.demo.inventory.domain.model.MaintenancePlan;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long> {
    @Query(value="select * from maintenance_plan mp WHERE mp.plant_id = ?1 AND mp.year_of_action = ?2 LIMIT 1", nativeQuery=true)
    MaintenancePlan getByPlantIdAndYearOfAction(Long plant_id, int year_of_action);

//    MaintenancePlan findByPlantAndYear_of_action(PlantInventoryItem plant, int year_of_action);
}
