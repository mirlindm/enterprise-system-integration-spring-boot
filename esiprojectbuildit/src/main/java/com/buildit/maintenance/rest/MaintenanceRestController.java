package com.buildit.maintenance.rest;

import com.buildit.maintenance.application.dto.MaintenanceOrderDTO;
import com.buildit.maintenance.application.dto.MaintenanceRequestDTO;
import com.buildit.maintenance.application.dto.PlantInventoryItemDTO;
import com.buildit.maintenance.application.service.MaintenanceService;
import com.buildit.maintenance.domain.model.MaintenanceOrder;
import com.buildit.maintenance.domain.model.MaintenanceRequest;
import com.buildit.common.rest.ExtendedLink;
import com.buildit.maintenance.domain.model.PlantInventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceRestController {

    private static final String GET_PLANTS = "http://localhost:8090/api/plants/items/";
    private static final String GET_MAINTENANCE = "http://localhost:8090/api/maintenance/";

    @Autowired
    MaintenanceService maintService;

    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @GetMapping("/plants/items/{id}")
    //@ResponseStatus(HttpStatus.OK)
    public PlantInventoryItemDTO getPlantItem(@PathVariable("id") Long id)  {
        return maintService.getPlantItem(new ExtendedLink(GET_PLANTS + id, "getMO", HttpMethod.GET));
    }

    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MaintenanceOrderDTO> getMaintenanceOrder(@PathVariable("id") Long id) {

        try {
            MaintenanceOrderDTO moDTO = maintService.getMaintenanceOrder(new ExtendedLink(GET_MAINTENANCE + "order/" + id, "getMO", HttpMethod.GET));
            return new ResponseEntity<MaintenanceOrderDTO>(moDTO, null, HttpStatus.OK);
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MaintenanceOrder Not Found", exc);
        }
    }

    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @PostMapping("/requests")
    public ResponseEntity<MaintenanceRequest> createMaintenanceRequest(@RequestBody MaintenanceRequestDTO maintenanceRequestDTO) throws Exception {
        MaintenanceRequest createRequest = maintService.createMaintenanceRequest(maintenanceRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+createRequest.getPlant().get_xlink().getHref());
        System.out.println(createRequest);
        PlantInventoryItemDTO piiDTO = maintService.getPlantItem(new ExtendedLink(createRequest.getPlant().get_xlink().getHref(), "getPlantItem", HttpMethod.GET));
        MaintenanceOrder createMaintenanceOrderDTO = maintService.createMaintenanceOrder(piiDTO, createRequest );
        MaintenanceRequest mr = maintService.findRequestById(createRequest.getId());
        return new ResponseEntity<MaintenanceRequest>(mr,headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @GetMapping("/requests/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MaintenanceRequestDTO> getMaintenanceRequest(@PathVariable("id") Long id)  throws Exception{
        try{
            MaintenanceRequestDTO moDTO = maintService.getMaintenanceRequest(id);
            return new ResponseEntity<MaintenanceRequestDTO>(moDTO, null, HttpStatus.OK) ;
        }
        catch (Exception exc){
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "MaintenanceRequest Not Found", exc);
        }
    }

    @PreAuthorize("hasAnyRole('SITE','WORKS', 'ADMIN')")
    @PatchMapping("/requests/{rid}/orders")
    public ResponseEntity<MaintenanceRequest> cancelMaintenanceRequest(@PathVariable Long rid) throws Exception {
        MaintenanceRequest mr = maintService.findRequestById(rid);
        mr = maintService.cancelMaintenanceOrder(mr);
        return new ResponseEntity<MaintenanceRequest>(mr,null, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        System.out.println("ERROR BEING HANDLED: "+ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), headers, HttpStatus.BAD_REQUEST);
    }


}
