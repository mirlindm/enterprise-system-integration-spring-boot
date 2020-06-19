package com.example.demo.models;

import com.example.demo.DemoApplication;
import javafx.util.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@Sql(scripts= "/plants-dataset.sql")
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class InventoryRepositoryTest {
    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepo;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepo;

    @Autowired
    PlantReservationRepository plantReservationRepo;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepo;

    @Autowired
    MaintenanceTaskRepository maintenanceTaskRepo;

    @Autowired
    MaintenancePlanRepository maintenancePlanRepo;

    @Autowired
    InventoryRepository inventoryRepo;

    @Test
    public void queryPlantCatalog() {
        assertThat(plantInventoryEntryRepo.count()).isEqualTo(14l);
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
    public void query1Test() {
        List<Pair<String, BigDecimal>> revenuePerEntry = inventoryRepo.query1();

        for ( Pair<String, BigDecimal> i : revenuePerEntry) {
            assertThat(i.getValue()).isEqualTo(new BigDecimal(0));
        }

        PlantInventoryEntry entry = plantInventoryEntryRepo.findById(2l).orElse(null);
        PlantInventoryItem item = plantInventoryItemRepo.findOneByPlantInfo(entry);
        String orderTotal = "1200.00";

        PurchaseOrder po = new PurchaseOrder();
        po.setTotal(new BigDecimal(orderTotal));
        po.setStatus(POStatus.CLOSED);
        po.setRentalPeriod(BusinessPeriod.of(LocalDate.of(2020, 2, 20),
                LocalDate.of(2020, 2, 25)));
        purchaseOrderRepo.save(po);

        PlantReservation pr = new PlantReservation();
        pr.setPlant(item);
        pr.setRental(po);
        plantReservationRepo.save(pr);

        List<Pair<String, BigDecimal>> revenuePerEntry2 = inventoryRepo.query1();

        for ( Pair<String, BigDecimal> i : revenuePerEntry2) {
            if (i.getKey() == entry.getName()) {
                assertThat(i.getValue()).isEqualTo(new BigDecimal(orderTotal));
            }
            else {
                assertThat(i.getValue()).isEqualTo(new BigDecimal(0));
            }
        }

    }

    @Test
    public void query2Test() {

        PlantInventoryEntry entry = plantInventoryEntryRepo.findById(1l).orElse(null);
        PlantInventoryItem item = plantInventoryItemRepo.findOneByPlantInfo(entry);

        PlantReservation pr = new PlantReservation();
        pr.setPlant(item);
        pr.setSchedule(BusinessPeriod.of(LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 25)));
        plantReservationRepo.save(pr);

        String name = entry.getName();
        List<Pair<String, Long>> datePairs = inventoryRepo.query2(LocalDate.of(2020,1,01), LocalDate.of(2020,1,30));

        for ( Pair<String, Long> i : datePairs) {
            assertThat(i.getValue()).isEqualTo(29l);
        }

        List<Pair<String, Long>> datePairs2 = inventoryRepo.query2(LocalDate.of(2020,2,01), LocalDate.of(2020,2,28));
        for ( Pair<String, Long> i : datePairs2) {
            if (i.getKey() == name) {
                assertThat(i.getValue()).isEqualTo(22l);
            }
            else {
                assertThat(i.getValue()).isEqualTo(27l);
            }
        }

    }


    @Test
    public void query2Test2() {

        PlantInventoryEntry entry = plantInventoryEntryRepo.findById(1l).orElse(null);
        PlantInventoryItem item = plantInventoryItemRepo.findOneByPlantInfo(entry);

        PlantReservation pr = new PlantReservation();
        pr.setPlant(item);
        pr.setSchedule(BusinessPeriod.of(LocalDate.of(2020, 1, 20), LocalDate.of(2020, 2, 25)));
        plantReservationRepo.save(pr);

        String name = entry.getName();

        List<Pair<String, Long>> datePairs2 = inventoryRepo.query2(LocalDate.of(2020,2,01), LocalDate.of(2020,2,28));
        for ( Pair<String, Long> i : datePairs2) {
            if (i.getKey() == name) {
                assertThat(i.getValue()).isEqualTo(3l);
            }
            else {
                assertThat(i.getValue()).isEqualTo(27l);
            }
        }

    }

    @Test
    public void query2Test3() {

        PlantInventoryEntry entry = plantInventoryEntryRepo.findById(1l).orElse(null);
        PlantInventoryItem item = plantInventoryItemRepo.findOneByPlantInfo(entry);

        PlantReservation pr = new PlantReservation();
        pr.setPlant(item);
        pr.setSchedule(BusinessPeriod.of(LocalDate.of(2020, 1, 20), LocalDate.of(2020, 2, 11)));
        plantReservationRepo.save(pr);

        PlantReservation pr2 = new PlantReservation();
        pr2.setPlant(item);
        pr2.setSchedule(BusinessPeriod.of(LocalDate.of(2020, 2, 11), LocalDate.of(2020, 2, 25)));
        plantReservationRepo.save(pr2);

        String name = entry.getName();

        List<Pair<String, Long>> datePairs2 = inventoryRepo.query2(LocalDate.of(2020,2,01), LocalDate.of(2020,2,28));
        for ( Pair<String, Long> i : datePairs2) {
            if (i.getKey() == name) {
                assertThat(i.getValue()).isEqualTo(3l);
            }
            else {
                assertThat(i.getValue()).isEqualTo(27l);
            }
        }

    }

    @Test
    /* UNAVAILABLE
        When plant is UNSERVICEABLECONDEMNED
    */
    public void query3test1(){
        String plantName = "Mini excavator";
        LocalDate startDate = LocalDate.of(2020,3,27);
        LocalDate endDate = LocalDate.of(2020,3,28);

        List<Pair<String, Long>> availableItems = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems).contains(new Pair<>("Mini excavator",(long) 2));

        PlantInventoryItem pi = plantInventoryItemRepo.findOneBySerialNumber("A02");
        pi.setEquipmentCondition(EquipmentCondition.UNSERVICEABLECONDEMNED);
        plantInventoryItemRepo.save(pi);

        List<Pair<String, Long>> availableItems2 = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems2).contains(new Pair<>("Mini excavator",(long) 1));
    }

    @Test
    /* UNAVAILABLE
        When plant = ( UNSERVICEABLEREPAIRABLE, UNSERVICEABLEINCOMPLETE)
            and Requested_start_date_to_query3  < 21 days from today
    */
    public void query3test2(){
        String plantName = "Mini excavator";
        LocalDate startDate = LocalDate.now().plusDays(14);
        LocalDate endDate = LocalDate.now().plusDays(19);

        List<Pair<String, Long>> availableItems = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems).contains(new Pair<>("Mini excavator",(long) 2));

        PlantInventoryItem pi = plantInventoryItemRepo.findOneBySerialNumber("A02");
        pi.setEquipmentCondition(EquipmentCondition.UNSERVICEABLEINCOMPLETE);
        plantInventoryItemRepo.save(pi);

        PlantReservation pr = new PlantReservation();
        pr.setPlant(pi);
        pr.setSchedule(BusinessPeriod.of(startDate, endDate));
        plantReservationRepo.save(pr);

        List<Pair<String, Long>> availableItems2 = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems2).contains(new Pair<>("Mini excavator",(long) 1));
    }

    @Test
    /* UNAVAILABLE
        When plant = ( UNSERVICEABLEREPAIRABLE, UNSERVICEABLEINCOMPLETE)
            and no scheduled maintenance task is created anytime between today
            and (Requested_start_date_to_query3 - 7)
    */
    public void query3test3(){
        String plantName = "Mini excavator";
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(9);

        List<Pair<String, Long>> availableItems = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems).contains(new Pair<>("Mini excavator",(long) 2));

        PlantInventoryItem pi = plantInventoryItemRepo.findOneBySerialNumber("A02");
        pi.setEquipmentCondition(EquipmentCondition.UNSERVICEABLEINCOMPLETE);
        plantInventoryItemRepo.save(pi);

        PlantReservation pr = new PlantReservation();
        pr.setPlant(pi);
        pr.setSchedule(BusinessPeriod.of(startDate, endDate));
        plantReservationRepo.save(pr);

        List<Pair<String, Long>> availableItems2 = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems2).contains(new Pair<>("Mini excavator",(long) 1));
    }

    @Test
    /* AVAILABLE
        When plant = ( UNSERVICEABLEREPAIRABLE, UNSERVICEABLEINCOMPLETE)
            and Requested_start_date_to_query3  > 21 days from today
            and there is at least one scheduled maintenance task is created anytime
                between today and (Requested_start_date_to_query3 - 7)
    */
    public void query3test4(){
        String plantName = "Mini excavator";
        LocalDate startDate = LocalDate.now().plusDays(24);
        LocalDate endDate = LocalDate.now().plusDays(29);

        List<Pair<String, Long>> availableItems = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems).contains(new Pair<>("Mini excavator",(long) 2));

        PlantInventoryItem pi = plantInventoryItemRepo.findOneBySerialNumber("A02");
        pi.setEquipmentCondition(EquipmentCondition.UNSERVICEABLEINCOMPLETE);
        plantInventoryItemRepo.save(pi);

        PlantReservation pr = new PlantReservation();
        pr.setPlant(pi);
        pr.setSchedule(BusinessPeriod.of(LocalDate.now().plusDays(7),LocalDate.now().plusDays(9)));
        plantReservationRepo.save(pr);

        MaintenanceTask mt = new MaintenanceTask();
        mt.setReservation(pr);
        mt.setRentalPeriod(BusinessPeriod.of(LocalDate.now().plusDays(7),LocalDate.now().plusDays(9)));
        maintenanceTaskRepo.save(mt);

        List<Pair<String, Long>> availableItems2 = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems2).contains(new Pair<>("Mini excavator",(long) 2));
    }

    @Test
    /* AVAILABLE
        When plant = ( UNSERVICEABLEREPAIRABLE, UNSERVICEABLEINCOMPLETE)
            and Requested_start_date_to_query3  > 21 days from today
            and there is at least one scheduled maintenance task is created anytime
                between today and (Requested_start_date_to_query3 - 7)
    */
    public void query3test5(){
        String plantName = "Mini excavator";
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(9);

        List<Pair<String, Long>> availableItems = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems).contains(new Pair<>("Mini excavator",(long) 2));

        PlantInventoryItem pi = plantInventoryItemRepo.findOneBySerialNumber("A02");
        pi.setEquipmentCondition(EquipmentCondition.UNSERVICEABLEINCOMPLETE);
        plantInventoryItemRepo.save(pi);

        PlantReservation pr = new PlantReservation();
        pr.setPlant(pi);
        pr.setSchedule(BusinessPeriod.of(LocalDate.now().plusDays(3),LocalDate.now().plusDays(11)));
        plantReservationRepo.save(pr);

        List<Pair<String, Long>> availableItems2 = inventoryRepo.query3(plantName,startDate,endDate);
        assertThat(availableItems2).contains(new Pair<>("Mini excavator",(long) 1));
    }

    @Test
    // Tests if items are ordered by number of maintenance.
    public void query4test1(){
        List<String> topThreeMaintainedItems = inventoryRepo.query4();
        List<String> expectedTopThreeMaintainedItems = Arrays.asList("A03","A02","A01");

        assertThat(topThreeMaintainedItems).isEqualTo(expectedTopThreeMaintainedItems);

        PlantInventoryItem item = plantInventoryItemRepo.findOneBySerialNumber("A02");

        PlantReservation pr = new PlantReservation();
        pr.setPlant(item);
        plantReservationRepo.save(pr);

        MaintenanceTask mt = new MaintenanceTask();
        mt.setRentalPeriod(BusinessPeriod.of(LocalDate.of(2019, 12, 10),
                LocalDate.of(2019, 12, 15)));
        mt.setTotal(new BigDecimal("800.0"));
        mt.setType_of_work(TypeOfWork.CORRECTIVE);
        mt.setReservation(pr);

        List<MaintenanceTask> mtList = new ArrayList<MaintenanceTask>();
        mtList.add(mt);
        maintenanceTaskRepo.save(mt);

        MaintenancePlan mp = new MaintenancePlan();
        mp.setPlant(item);
        mp.setYear_of_action(2019);
        mp.setTasks(mtList);
        maintenancePlanRepo.save(mp);

        List<String> topThreeMaintainedItems2 = inventoryRepo.query4();
        List<String> expectedTopThreeMaintainedItems2 = Arrays.asList("A02","A03","A01");

        assertThat(topThreeMaintainedItems2).isEqualTo(expectedTopThreeMaintainedItems2);
    }

    @Test
    // Tests if items are ordered by total maintenance cost when number of maintenance are same.
    public void query4test2(){
        List<String> topThreeMaintainedItems = inventoryRepo.query4();
        List<String> expectedTopThreeMaintainedItems = Arrays.asList("A03","A02","A01");

        assertThat(topThreeMaintainedItems).isEqualTo(expectedTopThreeMaintainedItems);

        // Add plant reservation, maintenance task and maintenance plan to item "A02"
        PlantInventoryItem itemA02 = plantInventoryItemRepo.findOneBySerialNumber("A02");

        PlantReservation prA02 = new PlantReservation();
        prA02.setPlant(itemA02);
        plantReservationRepo.save(prA02);

        MaintenanceTask mtA02 = new MaintenanceTask();
        mtA02.setRentalPeriod(BusinessPeriod.of(LocalDate.of(2019, 12, 10),
                LocalDate.of(2019, 12, 15)));
        mtA02.setTotal(new BigDecimal("1200.0"));
        mtA02.setType_of_work(TypeOfWork.CORRECTIVE);
        mtA02.setReservation(prA02);

        List<MaintenanceTask> mtListA02 = new ArrayList<MaintenanceTask>();
        mtListA02.add(mtA02);
        maintenanceTaskRepo.save(mtA02);

        MaintenancePlan mpA02 = new MaintenancePlan();
        mpA02.setPlant(itemA02);
        mpA02.setYear_of_action(2019);
        mpA02.setTasks(mtListA02);
        maintenancePlanRepo.save(mpA02);

        // Add plant reservation, maintenance task and maintenance plan to item "A01"
        //      (setting different "total amount" for maintenance task)
        PlantInventoryItem itemA01 = plantInventoryItemRepo.findOneBySerialNumber("A01");

        PlantReservation prA01 = new PlantReservation();
        prA01.setPlant(itemA01);
        plantReservationRepo.save(prA01);

        MaintenanceTask mtA01 = new MaintenanceTask();
        mtA01.setRentalPeriod(BusinessPeriod.of(LocalDate.of(2019, 12, 10),
                LocalDate.of(2019, 12, 15)));
        mtA01.setTotal(new BigDecimal("1000.0"));
        mtA01.setType_of_work(TypeOfWork.CORRECTIVE);
        mtA01.setReservation(prA01);

        List<MaintenanceTask> mtListA01 = new ArrayList<MaintenanceTask>();
        mtListA01.add(mtA01);
        maintenanceTaskRepo.save(mtA01);

        MaintenancePlan mpA01 = new MaintenancePlan();
        mpA01.setPlant(itemA01);
        mpA01.setYear_of_action(2019);
        mpA01.setTasks(mtListA01);
        maintenancePlanRepo.save(mpA01);

        List<String> topThreeMaintainedItems2 = inventoryRepo.query4();
        List<String> expectedTopThreeMaintainedItems2 = Arrays.asList("A02","A01","A03");

        assertThat(topThreeMaintainedItems2).isEqualTo(expectedTopThreeMaintainedItems2);
    }

}