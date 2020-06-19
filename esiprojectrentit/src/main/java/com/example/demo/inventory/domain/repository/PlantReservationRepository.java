package com.example.demo.inventory.domain.repository;

import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantReservationRepository extends JpaRepository<PlantReservation, Long> {
    List<PlantReservation> findAllByPlant(PlantInventoryItem item);
    PlantReservation findByPlantId(Long id);

    @Query(value="select r from PlantReservation r where r.rental.id= ?1")
    PlantReservation findReservationsByRentalId(Long rentalId);

    @Query(value="select r from PlantReservation r where r.plant.id= ?1")
    List<PlantReservation> findAllReservationsByPlantId(Long plantId);

}
