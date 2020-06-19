package com.example.demo.inventory.application.service;

import com.example.demo.common.ErrorResponseHelper;
import com.example.demo.common.application.dto.PlantInventoryItemValidator;
import com.example.demo.common.application.dto.RequestBodyValidator;
import com.example.demo.inventory.application.dto.*;
import com.example.demo.inventory.domain.model.*;
import com.example.demo.inventory.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    @Autowired
    PlantInventoryEntryRepository entryRepository;

    @Autowired
    PlantInventoryItemRepository itemRepository;

    @Autowired
    PlantInventoryEntryAssembler entryAssembler;

    @Autowired
    PlantInventoryItemAssembler itemAssembler;

    @Autowired
    MaintenanceTaskRepository taskRepository;

    @Autowired
    MaintenanceTaskAssembler taskAssembler;

    @Autowired
    MaintenancePlanRepository planRepository;

    @Autowired
    PlantReservationRepository reservationRepository;

    public List<PlantInventoryEntryDTO> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate){
        // Complete the implementation here -- assembler required
        // Remove the return of the empty list
        return new ArrayList<PlantInventoryEntryDTO>();
    }

    public List<PlantInventoryEntryDTO> getAllPlantEntries(){
        System.out.println("getAllPlants");

        ArrayList<PlantInventoryEntryDTO> response = new ArrayList<>();
        List<PlantInventoryEntry> allEntries = entryRepository.findAll();
//        System.out.println(allEntries);

        for(PlantInventoryEntry entry: allEntries){
            response.add(entryAssembler.toResource(entry));
        }

        return response;
    }

    public PlantInventoryEntryDTO getPlantEntry(Long id){
        PlantInventoryEntry entry = entryRepository.findById(id).orElse(null);
        return entryAssembler.toResource(entry);
    }

    public List<PlantInventoryItemDTO> getPlantItems(Long id){
        PlantInventoryEntry entry = entryRepository.findById(id).orElse(null);
        List<PlantInventoryItem> items = itemRepository.findAllByPlantInfo(entry);

        List<PlantInventoryItemDTO> response = new ArrayList<>();
        for(PlantInventoryItem item: items){
            response.add(itemAssembler.toResource(item));
        }

        return response;
    }

    public List<MaintenanceTaskDTO> getMaintenanceTasks(Long id, Boolean onlyAlreadyPerformed){
        PlantInventoryItem item = itemRepository.findById(id).orElse(null);
        List<MaintenanceTask> allTasksOfCurrentItem = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // Collect "ALL" maintenance tasks from reservations
        for(PlantReservation reservation: reservationRepository.findAllByPlant(item)){
            List<MaintenanceTask> taskListOfCurrentReservation = taskRepository.findAllByReservation(reservation);
            allTasksOfCurrentItem.addAll(taskListOfCurrentReservation);
        }

        // Filter tasks based on their 'startDate'
        Predicate<MaintenanceTask> isInThePast = task -> now.compareTo(task.getRentalPeriod().getStartDate()) > 0;
        Predicate<MaintenanceTask> isInTheFuture = task -> now.compareTo(task.getRentalPeriod().getStartDate()) < 0;

        if(onlyAlreadyPerformed){
            allTasksOfCurrentItem = allTasksOfCurrentItem.stream().filter(isInThePast).collect(Collectors.toList());
        }else{
            allTasksOfCurrentItem = allTasksOfCurrentItem.stream().filter(isInTheFuture).collect(Collectors.toList());
        }

        // Send response
        List<MaintenanceTaskDTO> response = new ArrayList<>();

        for(MaintenanceTask task : allTasksOfCurrentItem){
            response.add(taskAssembler.toResource(task));
        }

        return response;
    }

    public PlantInventoryItemDTO updatePlantItemStatus(Long id, String newstatus) {
        PlantInventoryItem item = itemRepository.findById(id).orElse(null);
        item.setEquipmentCondition(EquipmentCondition.valueOf(newstatus));
        itemRepository.save(item);
        PlantInventoryItem item2 = itemRepository.findById(id).orElse(null);
        return itemAssembler.toResource(item2);
    }

    public PlantInventoryItemDTO updatePlantItemStatus_v2(Long id, PlantInventoryItemDTO item) {
        PlantInventoryItem item_db = itemRepository.findById(id).orElse(null);
        item_db.setEquipmentCondition(item.getEquipmentCondition());
        itemRepository.save(item_db);
        return itemAssembler.toResource(item_db);
    }

    public MaintenanceTaskDTO createMaintenanceTask(Long id, MaintenanceTaskRequest maintenanceTaskRequest) throws Exception {
        PlantInventoryItem plantInventoryItem = itemRepository.findById(id).orElse(new PlantInventoryItem());
        Map<String, List<String>> allErrors = new HashMap<>();
        BusinessPeriod period = BusinessPeriod.of(maintenanceTaskRequest.getRentalPeriod().getStartDate(), maintenanceTaskRequest.getRentalPeriod().getEndDate());
        TypeOfWork workType = maintenanceTaskRequest.getType_of_work();
        int current_year = LocalDate.now().getYear();

        // Validate plant item
        DataBinder itemBinder = new DataBinder(plantInventoryItem);
        itemBinder.addValidators(new PlantInventoryItemValidator());
        itemBinder.validate();

        if (itemBinder.getBindingResult().hasErrors()){
            Map<String, List<String>> itemErrors = ErrorResponseHelper.objectErrorsToMap(itemBinder.getBindingResult().getAllErrors());
            allErrors.putAll(itemErrors);
            throw new Exception(ErrorResponseHelper.errorMapToJsonString(allErrors));
        }

        // Validate request body(total amount and dates)
        DataBinder requestBodyBinder = new DataBinder(maintenanceTaskRequest);
        requestBodyBinder.addValidators(new RequestBodyValidator());
        requestBodyBinder.validate();

        if (requestBodyBinder.getBindingResult().hasErrors()){
            Map<String, List<String>> requestBodyErrors = ErrorResponseHelper.objectErrorsToMap(requestBodyBinder.getBindingResult().getAllErrors());
            allErrors.putAll(requestBodyErrors);
        }

        // Validate TOW
        EquipmentCondition itemCondition = plantInventoryItem.getEquipmentCondition();
        TowEqCondition tt = new TowEqCondition();
        tt.setEquipmentCondition(itemCondition);
        tt.setTypeOfWork(workType);

        DataBinder towBinder = new DataBinder(tt);
        towBinder.addValidators(new TypeOfWorkValidator());
        towBinder.validate();

        if (towBinder.getBindingResult().hasErrors()){
            Map<String, List<String>> towErrors = ErrorResponseHelper.objectErrorsToMap(towBinder.getBindingResult().getAllErrors());
            allErrors.putAll(towErrors);
        }

        // Return all errors together
        if(!allErrors.isEmpty()){
            throw new Exception(ErrorResponseHelper.errorMapToJsonString(allErrors));
        }

        // CREATION OF MAINTENANCE ITEM

        // 1) create a new reservation
        PlantReservation plantReservation = new PlantReservation();
        plantReservation.setPlant(plantInventoryItem);
        plantReservation.setSchedule(period);
        reservationRepository.save(plantReservation);

        // 2) check if maintenance plan exists for this year. If not, create.
        MaintenancePlan maintenancePlan = planRepository.getByPlantIdAndYearOfAction(plantInventoryItem.getId(),current_year);
        if(maintenancePlan == null){
            maintenancePlan = new MaintenancePlan();
            maintenancePlan.setPlant(plantInventoryItem);
            maintenancePlan.setYear_of_action(current_year);
            planRepository.save(maintenancePlan);
        }

        // Save
        MaintenanceTask mTask = maintenanceTaskRequest.toMaintenanceTask(period,plantReservation,maintenancePlan);
        System.out.println(mTask);
        taskRepository.save(mTask);

        return taskAssembler.toResource(mTask);
    }

}
