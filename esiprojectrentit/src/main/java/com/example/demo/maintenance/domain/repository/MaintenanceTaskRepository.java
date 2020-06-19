package com.example.demo.maintenance.domain.repository;

import com.example.demo.maintenance.domain.model.MaintenanceOrder;
import com.example.demo.maintenance.domain.model.MaintenanceTask;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {
    List<MaintenanceTask> findAllByReservation(PlantReservation reservation);
    MaintenanceTask findByOrder(MaintenanceOrder maintenanceOrder);

    @Query(value="select * from maintenance_task ", nativeQuery=true)
    List<PlantInventoryEntry> get(String name);

    @Query(value="select * from maintenance_task mt join plant_reservation pr where mt.reservation_id = pr.id AND pr.plant_id = %?1%", nativeQuery=true)
    List<PlantInventoryEntry> getPerformedMaintenanceTasksByItemId(Long id);
}

