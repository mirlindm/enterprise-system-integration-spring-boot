package com.example.demo.maintenance.domain.repository;

import com.example.demo.maintenance.domain.model.MaintOrderStatus;
import com.example.demo.maintenance.domain.model.MaintenanceOrder;
import com.example.demo.maintenance.domain.model.MaintenancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceOrderRepository extends JpaRepository<MaintenanceOrder, Long> {

    List<MaintenanceOrder> findAllByConstructionSiteId(Long id);
    List<MaintenanceOrder> findAllByConstructionSiteIdAndAndStatus(Long id, MaintOrderStatus status);


}
