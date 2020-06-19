package com.buildit.maintenance.domain.repositories;

import com.buildit.maintenance.domain.model.MaintenanceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceOrderRepository extends JpaRepository<MaintenanceOrder, Long> {

}
