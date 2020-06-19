package com.example.demo.models;

import com.example.demo.DemoApplication;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.InventoryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.maintenance.domain.model.MaintenanceOrder;
import com.example.demo.maintenance.domain.repository.MaintenanceOrderRepository;
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
public class MaintenanceRepositoryTest {

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepo;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepo;

    @Autowired
    PlantReservationRepository plantReservationRepo;

    @Autowired
    InventoryRepository inventoryRepo;

    @Autowired
    MaintenanceOrderRepository maintenanceOrderRepo;

    @Test
    public void queryMaintenanceOrders() {
        assertThat(maintenanceOrderRepo.count()).isEqualTo(5l);
    }

    @Test
    public void queryById() {

        MaintenanceOrder p1 = maintenanceOrderRepo.findById(1L).orElse(null);
        MaintenanceOrder p2 = maintenanceOrderRepo.findById(2L).orElse(null);
        assertThat(p1).isNotNull();
        assertThat(p2).isNotNull();

        assertThat(p1.getSiteEngineerName()).isEqualTo("ENG 1");
        assertThat(p2.getSiteEngineerName()).isEqualTo("ENG 2");

    }

}
