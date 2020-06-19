package com.example.demo.maintenance.rest;

import com.example.demo.maintenance.application.dto.MaintenanceOrderDTO;
import com.example.demo.maintenance.application.dto.MaintenanceOrderRequestDTO;
import com.example.demo.maintenance.application.dto.MaintenanceTaskDTO;
import com.example.demo.maintenance.application.service.MaintenanceService;
import com.example.demo.maintenance.domain.model.MaintOrderStatus;
import com.example.demo.maintenance.domain.model.MaintenanceTaskRequest;
import com.example.demo.maintenance.domain.repository.MaintenanceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceRestController {

    @Autowired
    MaintenanceService maintService;

    @GetMapping("/items/{id}/tasks/performed")
    @ResponseStatus(HttpStatus.OK)
    public List<MaintenanceTaskDTO> fetchPerformedMaintenanceTasks(@PathVariable("id") Long id) {
        return maintService.getMaintenanceTasks(id, true);
    }

    @GetMapping("/items/{id}/tasks/scheduled")
    @ResponseStatus(HttpStatus.OK)
    public List<MaintenanceTaskDTO> fetchScheduledMaintenanceTasks(@PathVariable("id") Long id) {
        return maintService.getMaintenanceTasks(id, false);
    }

    @PostMapping("/items/{id}/tasks/create")
    public ResponseEntity<MaintenanceTaskDTO> createMaintenanceTask(@PathVariable("id") Long id, @RequestBody MaintenanceTaskRequest maintenanceTaskRequest) throws Exception{
        //System.out.println("In Controller");
        MaintenanceTaskDTO createdDTO = maintService.createMaintenanceTask(id, maintenanceTaskRequest);

        HttpHeaders headers = new HttpHeaders();
        //headers.setLocation(new URI(createdDTO.getId().getHref()));
        headers.setLocation(createdDTO.getRequiredLink(IanaLinkRelations.SELF).toUri());
        // The above line won't working until you update MaintenanceTaskDTO to extend ResourceSupport

        return new ResponseEntity<MaintenanceTaskDTO>(createdDTO, headers, HttpStatus.CREATED);
    }


    @PostMapping("/orders")
    public ResponseEntity<MaintenanceOrderDTO> createMaintenanceOrder(@RequestBody MaintenanceOrderRequestDTO maintenanceOrderRequestDTO) throws Exception {
        MaintenanceOrderDTO createOrderDto = maintService.createMaintenanceOrder(maintenanceOrderRequestDTO);

        HttpHeaders headers = new HttpHeaders();
        //headers.setLocation(new URI(createdDTO.getId().getHref()));
        headers.setLocation(createOrderDto.getRequiredLink(IanaLinkRelations.SELF).toUri());
        // The above line won't working until you update MaintenanceTaskDTO to extend ResourceSupport

        return new ResponseEntity<MaintenanceOrderDTO>(createOrderDto, headers, HttpStatus.CREATED);
    }


    @GetMapping("/order/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MaintenanceOrderDTO fetchMaintenanceOrders(@PathVariable("id") Long id) throws Exception {
        return maintService.getMaintenanceOrderById(id);
    }


    @GetMapping(value="/order", params="constSiteId")
    @ResponseStatus(HttpStatus.OK)
    public List<MaintenanceOrderDTO> fetchOrderByConstructionSite(@RequestParam Long constSiteId){
        return maintService.getOrdersByConstructionSite(constSiteId);
    }

    @GetMapping(value="/order", params={"constSiteId", "status"})
    @ResponseStatus(HttpStatus.OK)
    public List<MaintenanceOrderDTO> fetchOrderByConstIdAndStatus(@RequestParam Long constSiteId, @RequestParam String status) {
        return maintService.getOrderByConstIdAndStatus(constSiteId,status);
    }

    @PutMapping("/order/{id}")
    public MaintenanceOrderDTO acceptMaintenanceOrder(@PathVariable("id") Long id, @RequestBody MaintenanceTaskRequest maintenanceTaskRequest) throws Exception {
        return maintService.acceptMaintenanceOrder(id,maintenanceTaskRequest);
    }

    @DeleteMapping("/order/{id}")
    public MaintenanceOrderDTO rejectMaintenanceOrder(@PathVariable("id") Long id){
        return maintService.rejectMaintenanceOrder(id);
    }

    @PatchMapping("/order/{id}")
    public MaintenanceOrderDTO completeMaintenanceOrder(@PathVariable("id") Long id){
        return maintService.completeMaintenanceOrder(id);
    }

    @PatchMapping("order/{id}/cancel")
    public ResponseEntity<MaintenanceOrderDTO> cancelMaintenanceOrder(@PathVariable("id") Long id) throws Exception{
            MaintenanceOrderDTO moDTO = maintService.cancelMaintenanceOrder(id);
            return new ResponseEntity<MaintenanceOrderDTO>(moDTO,null, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        System.out.println("ERROR BEING HANDLED: "+ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.BAD_REQUEST);
    }

}
