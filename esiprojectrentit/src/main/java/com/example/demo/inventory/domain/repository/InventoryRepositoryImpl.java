package com.example.demo.inventory.domain.repository;

import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryRepositoryImpl implements CustomInventoryRepository {

    @Autowired
    EntityManager em;

    public List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select p.plantInfo from PlantInventoryItem p where p.plantInfo.name like concat('%', ?1, '%') and p not in" +
                "(select r.plant from PlantReservation r where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)",
                PlantInventoryEntry.class)
                .setParameter(1, name)
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .getResultList();
    }

    public List<PlantInventoryItem> findAvailableItems(String name, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select p from PlantInventoryItem p where p.plantInfo.name like ?1 and p.equipmentCondition='SERVICEABLE' and p not in " +
                        "(select r.plant from PlantReservation r where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)",
                PlantInventoryItem.class)
                .setParameter(1, name)
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .getResultList();
    }
}
