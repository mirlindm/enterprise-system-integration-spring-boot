package com.example.demo.models;

import com.example.demo.DemoApplication;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.inventory.domain.model.*;
import com.example.demo.inventory.domain.repository.InventoryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
//@Sql(scripts="/plants-dataset.sql")
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class InventoryRepositoryTest {
    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepo;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepo;

    @Autowired
    PlantReservationRepository plantReservationRepo;

    @Autowired
    InventoryRepository inventoryRepo;

    @Autowired
    PlantInventoryItemRepository itemRepository;

    @Test
    public void queryPlantCatalog() {
        assertThat(plantInventoryEntryRepo.count()).isEqualTo(4l);
    }

    @Test
    public void queryByName() {
        assertThat(plantInventoryEntryRepo.findByNameContaining("Mini").size()).isEqualTo(2);

        PlantInventoryEntry p1 = plantInventoryEntryRepo.findById(1L).orElse(null);
        PlantInventoryEntry p2 = plantInventoryEntryRepo.findById(2L).orElse(null);
        assertThat(p1).isNotNull();
        assertThat(p1).isNotNull();

        List<PlantInventoryEntry> res1 = plantInventoryEntryRepo.finderMethod("Mini");
        List<PlantInventoryEntry> res2 = plantInventoryEntryRepo.finderMethodV2("Mini");

        assertThat(res1).containsExactly(p1, p2);
        assertThat(res2).containsExactly(p1, p2);
    }

    @Test
    public void findAvailableTest() {
        PlantInventoryEntry entry = plantInventoryEntryRepo.findById(1l).orElse(null);
        PlantInventoryItem item = plantInventoryItemRepo.findOneByPlantInfo(entry);

        assertThat(inventoryRepo.findAvailablePlants("Mini", LocalDate.of(2020,2,20), LocalDate.of(2020,2,25)))
                .contains(entry);

        PlantReservation po = new PlantReservation();
        po.setPlant(item);
        po.setSchedule(BusinessPeriod.of(LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 25)));
        plantReservationRepo.save(po);

        assertThat(inventoryRepo.findAvailablePlants("Mini", LocalDate.of(2020,2,20), LocalDate.of(2020,2,25)))
                .doesNotContain(entry);
    }

    @Test
    public void updatePlantStatus() {
        PlantInventoryItem item = itemRepository.findById(1L).orElse(null);
        assertThat(item.getEquipmentCondition()).isEqualTo(EquipmentCondition.SERVICEABLE);

        //PlantInventoryItemDTO ss = inventoryService.updatePlantItemStatus(1L, EquipmentCondition.UNSERVICEABLECONDEMNED);
        PlantInventoryItemDTO ss = inventoryService.updatePlantItemStatus(1L, "UNSERVICEABLECONDEMNED");
        PlantInventoryItem item2 = itemRepository.findById(1L).orElse(null);
        System.out.println(ss);
        assertThat(item2.getEquipmentCondition()).isEqualTo(EquipmentCondition.UNSERVICEABLECONDEMNED);
    }

    @Test
    public void updatePlantStatus2() {
        PlantInventoryItem item = itemRepository.findById(1L).orElse(null);
        assertThat(item.getEquipmentCondition()).isEqualTo(EquipmentCondition.SERVICEABLE);

        //PlantInventoryItemDTO ss = inventoryService.updatePlantItemStatus(1L, EquipmentCondition.UNSERVICEABLECONDEMNED);
        PlantInventoryItemDTO ss = inventoryService.updatePlantItemStatus(1L, "UNSERVICEABLECONDEMNED");
        PlantInventoryItem item2 = itemRepository.findById(1L).orElse(null);
        System.out.println(ss);
        assertThat(item2.getEquipmentCondition()).isEqualTo(EquipmentCondition.UNSERVICEABLECONDEMNED);
    }
}
