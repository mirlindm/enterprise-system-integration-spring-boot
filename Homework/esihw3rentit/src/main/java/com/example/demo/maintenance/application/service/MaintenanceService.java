package com.example.demo.maintenance.application.service;
import com.example.demo.common.application.ErrorResponseHelper;
import com.example.demo.common.application.dto.MaintenanceOrderRequestBodyValidator;
import com.example.demo.common.application.dto.PlantInventoryItemValidator;
import com.example.demo.common.application.dto.RequestBodyValidator;
import com.example.demo.inventory.application.service.PlantInventoryEntryAssembler;
import com.example.demo.maintenance.application.dto.MaintenanceOrderDTO;
import com.example.demo.maintenance.application.dto.MaintenanceOrderRequestDTO;
import com.example.demo.maintenance.application.dto.MaintenanceTaskDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.maintenance.application.dto.TowEqCondition;
import com.example.demo.inventory.domain.model.*;
import com.example.demo.inventory.domain.repository.*;
import com.example.demo.maintenance.application.service.MaintenanceTaskAssembler;
import com.example.demo.maintenance.application.service.TypeOfWorkValidator;
import com.example.demo.maintenance.domain.model.*;
import com.example.demo.maintenance.domain.repository.MaintenanceOrderRepository;
import com.example.demo.maintenance.domain.repository.MaintenancePlanRepository;
import com.example.demo.maintenance.domain.repository.MaintenanceTaskRepository;
import com.example.demo.maintenance.rest.MaintenanceRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MaintenanceService {

    @Autowired
    MaintenanceTaskRepository taskRepository;

    @Autowired
    MaintenanceOrderRepository orderRepository;

    @Autowired
    MaintenanceTaskAssembler taskAssembler;

    @Autowired
    MaintenanceOrderAssembler orderAssembler;

    @Autowired
    PlantInventoryEntryAssembler entryAssembler;

    @Autowired
    MaintenancePlanRepository planRepository;

    @Autowired
    PlantInventoryItemRepository itemRepository;

    @Autowired
    PlantReservationRepository reservationRepository;

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
            response.add(taskAssembler.toModel(task));
        }

        return response;
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
        mTask.setOrder(maintenanceTaskRequest.getMaintenanceOrder());
        System.out.println(mTask);
        taskRepository.save(mTask);

        return taskAssembler.toModel(mTask);
    }


    public MaintenanceOrderDTO createMaintenanceOrder(MaintenanceOrderRequestDTO maintenanceOrderRequestDTO) throws Exception {
        Map<String, List<String>> allErrors = new HashMap<>();
        //check if plant item exists
        PlantInventoryItem plantInventoryItem = itemRepository.findById(maintenanceOrderRequestDTO.getPlantID()).orElse(null);

        // Validate plant item
        DataBinder itemBinder = new DataBinder(plantInventoryItem);
        itemBinder.addValidators(new PlantInventoryItemValidator());
        itemBinder.validate();

        if (itemBinder.getBindingResult().hasErrors()){
            Map<String, List<String>> itemErrors = ErrorResponseHelper.objectErrorsToMap(itemBinder.getBindingResult().getAllErrors());
            allErrors.putAll(itemErrors);
            throw new Exception(ErrorResponseHelper.errorMapToJsonString(allErrors));
        }

        // Validate request body(dates and other fields)
        DataBinder requestBodyBinder = new DataBinder(maintenanceOrderRequestDTO);
        requestBodyBinder.addValidators(new MaintenanceOrderRequestBodyValidator());
        requestBodyBinder.validate();

        if (requestBodyBinder.getBindingResult().hasErrors()){
            Map<String, List<String>> requestBodyErrors = ErrorResponseHelper.objectErrorsToMap(requestBodyBinder.getBindingResult().getAllErrors());
            allErrors.putAll(requestBodyErrors);
        }

        // Return all errors together
        if(!allErrors.isEmpty()){
            throw new Exception(ErrorResponseHelper.errorMapToJsonString(allErrors));
        }


        //Fetch businessPeriod,constructionSiteId,description and siteEngineerName
        BusinessPeriod businessPeriod = BusinessPeriod.of(maintenanceOrderRequestDTO.getExpectedPeriod().getStartDate(),maintenanceOrderRequestDTO.getExpectedPeriod().getEndDate());
        Long constructionSiteId = maintenanceOrderRequestDTO.getConstructionSiteId();
        String description = maintenanceOrderRequestDTO.getDescription();
        String siteEngineerName=maintenanceOrderRequestDTO.getSiteEngineerName();

        //create maintenance order
        MaintenanceOrder mOrder = maintenanceOrderRequestDTO.toMaintenanceOrder(businessPeriod,constructionSiteId,description,siteEngineerName,plantInventoryItem);
        System.out.println(mOrder);
        orderRepository.save(mOrder);

        return orderAssembler.toModel(mOrder);

    }

    public MaintenanceOrderDTO getMaintenanceOrderById(Long id) throws Exception {
        MaintenanceOrder orderById = orderRepository.findById(id).orElse(null);
        if (orderById == null) {
            List<String> list = Stream.of("Maintenance Order Not Found").collect(Collectors.toList());
            throw new Exception(ErrorResponseHelper.stringListToJson(list));
        }
        return orderAssembler.toModel(orderById);
    }


    public List<MaintenanceOrderDTO> getOrdersByConstructionSite(Long constSiteId){

        List<MaintenanceOrder> orderByConstructionSite = orderRepository.findAllByConstructionSiteId(constSiteId);
        List<MaintenanceOrderDTO> orderResponse = new ArrayList<>();
        for (MaintenanceOrder orders: orderByConstructionSite) {
            orderResponse.add(orderAssembler.toModel(orders));
        }

        return orderResponse;
    }


    public List<MaintenanceOrderDTO> getOrderByConstIdAndStatus(Long constSiteId, String status){


        List<MaintenanceOrder> orderByConstIdAndStatus = orderRepository.findAllByConstructionSiteIdAndAndStatus(constSiteId,MaintOrderStatus.valueOf(status));

        List<MaintenanceOrderDTO> orderConstAndStatusResponse = new ArrayList<>();
        for (MaintenanceOrder orders: orderByConstIdAndStatus) {
            orderConstAndStatusResponse.add(orderAssembler.toModel(orders));
        }
        return orderConstAndStatusResponse;
    }



    public MaintenanceOrderDTO acceptMaintenanceOrder(Long id, MaintenanceTaskRequest maintenanceTaskRequest) throws Exception {
        MaintenanceOrder orderExists = orderRepository.findById(id).orElse(null);
        orderExists.setStatus(MaintOrderStatus.ACCEPTED);
        maintenanceTaskRequest.setMaintenanceOrder(orderExists);

        MaintenanceTaskDTO mtDTO = createMaintenanceTask(orderExists.getPlant().getId(),maintenanceTaskRequest);
        MaintenanceTask mt = taskRepository.findById(mtDTO.get_id()).orElse(null);
        orderRepository.save(orderExists);

        return orderAssembler.toModel(orderExists);

    }

    public MaintenanceOrderDTO rejectMaintenanceOrder(Long id){
        MaintenanceOrder orderExists = orderRepository.findById(id).orElse(null);
        orderExists.setStatus(MaintOrderStatus.REJECTED);
        orderRepository.save(orderExists);

        return orderAssembler.toModel(orderExists);
    }


    public MaintenanceOrderDTO completeMaintenanceOrder(Long id){
        MaintenanceOrder orderExists = orderRepository.findById(id).orElse(null);
        orderExists.setStatus(MaintOrderStatus.COMPLETED);
        orderRepository.save(orderExists);

        return orderAssembler.toModel(orderExists);
    }

    public MaintenanceOrderDTO cancelMaintenanceOrder(Long id) throws Exception{
        // Check if order status is accepted and current date is less than start date of maintenance reservation
        MaintenanceOrder order = orderRepository.findById(id).orElse(null);
        PlantReservation reservation = reservationRepository.findByPlantId(order.getPlant().getId());
        MaintenanceTask maintenanceTask = taskRepository.findByOrder(order);

        if(order.getStatus() == MaintOrderStatus.PENDING){
            order.setStatus(MaintOrderStatus.CANCELLED);
            orderRepository.save(order);
            return orderAssembler.toModel(order);
        }

        if(order.getStatus() == MaintOrderStatus.ACCEPTED && LocalDate.now().isBefore(reservation.getSchedule().getStartDate())){
            order.setStatus(MaintOrderStatus.CANCELLED);
            orderRepository.save(order);
            taskRepository.delete(maintenanceTask);
            reservationRepository.delete(reservation);

            return orderAssembler.toModel(order);
        }
        List<String> list = Stream.of("Maintenance Order Can't be cancelled").collect(Collectors.toList());
        throw new Exception(ErrorResponseHelper.stringListToJson(list));

    }



}
