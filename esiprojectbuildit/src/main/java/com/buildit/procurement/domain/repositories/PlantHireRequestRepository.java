package com.buildit.procurement.domain.repositories;

import com.buildit.procurement.domain.model.PlantHireRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlantHireRequestRepository extends JpaRepository<PlantHireRequest, Long> {
    @Query(value = "select * from PLANT_HIRE_REQUEST p WHERE p.po_id = ?1 LIMIT 1", nativeQuery=true)
    Optional<PlantHireRequest> getPlantHireRequestByPo(Long poId);
}
