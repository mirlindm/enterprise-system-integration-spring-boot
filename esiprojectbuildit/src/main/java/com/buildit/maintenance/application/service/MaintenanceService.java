package com.buildit.maintenance.application.service;

import com.buildit.common.application.ErrorResponseHelper;
import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.common.rest.ExtendedLink;
import com.buildit.maintenance.application.dto.MaintenanceOrderDTO;
import com.buildit.maintenance.application.dto.MaintenanceRequestDTO;
import com.buildit.maintenance.application.dto.PlantInventoryItemDTO;
import com.buildit.maintenance.domain.model.MRStatus;
import com.buildit.maintenance.domain.model.MaintenanceOrder;
import com.buildit.maintenance.domain.model.MaintenanceRequest;
import com.buildit.maintenance.domain.model.PlantInventoryItem;
import com.buildit.maintenance.domain.repositories.MaintenanceOrderRepository;
import com.buildit.maintenance.domain.repositories.MaintenanceRequestRepository;
import com.buildit.maintenance.domain.repositories.PlantInventoryItemRepository;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repositories.PlantHireRequestRepository;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MaintenanceService {
    private static final String BASE_URL = "http://localhost:8090/api/maintenance/";
    private static final String GET_MAINTENENACE = "http://localhost:8090/api/maintenance/";
    private static final String GET_PLANTS = "http://localhost:8090/api/plants/items/";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MaintenanceOrderRepository maintenanceOrderRepository;

    @Autowired
    MaintenanceRequestRepository maintenanceRequestRepository;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;


    public MaintenanceOrderDTO getMaintenanceOrder(ExtendedLink requestLink) {
        return restTemplate.getForObject(requestLink.getHref(), MaintenanceOrderDTO.class);
    }

    public MaintenanceRequest createMaintenanceRequest(MaintenanceRequestDTO maintenanceRequestDTO) {
        MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
        maintenanceRequest.setSiteEngineerName(maintenanceRequestDTO.getSiteEngineerName());
        maintenanceRequest.setConstructionSiteId(maintenanceRequestDTO.getConstructionSiteId());

        BusinessPeriod expectedPeriod = BusinessPeriod.of(
                maintenanceRequestDTO.getExpectedPeriod().getStartDate(),
                maintenanceRequestDTO.getExpectedPeriod().getEndDate());
        maintenanceRequest.setExpectedPeriod(expectedPeriod);
        maintenanceRequest.setDescription(maintenanceRequestDTO.getDescription());
        maintenanceRequest.setIssueDate(LocalDate.now());
        PlantInventoryItem plant = PlantInventoryItem.of(maintenanceRequestDTO, GET_PLANTS);
        maintenanceRequest.setPlant(plant);
        plantInventoryItemRepository.save(plant);

        maintenanceRequest.setStatus(MRStatus.PENDING);
        MaintenanceRequest mr = maintenanceRequestRepository.save(maintenanceRequest);

        return mr;
    }

    public List<MaintenanceRequest> getMaintenanceRequests() {
        return maintenanceRequestRepository.findAll();
    }

    public PlantInventoryItemDTO getPlantItem(ExtendedLink requestLink) {
        return restTemplate.getForObject(requestLink.getHref(), PlantInventoryItemDTO.class);
    }

    public PlantInventoryItem findPlantById(Long id) {
        return plantInventoryItemRepository.findById(id).orElse(null);
    }

    public MaintenanceRequest findRequestById(Long id) {
        return maintenanceRequestRepository.findById(id).orElse(null);
    }


    public MaintenanceOrder createMaintenanceOrder(PlantInventoryItemDTO plantInventoryItemDTO, MaintenanceRequest maintenanceRequest) {
        MaintenanceOrderDTO maintenanceOrderDTO = new MaintenanceOrderDTO();
        maintenanceOrderDTO.setConstructionSiteId(maintenanceRequest.getConstructionSiteId());
        maintenanceOrderDTO.setExpectedPeriod(maintenanceRequest.getExpectedPeriod());
        maintenanceOrderDTO.setSiteEngineerName(maintenanceRequest.getSiteEngineerName());
        maintenanceOrderDTO.setDescription(maintenanceRequest.getDescription());
        maintenanceOrderDTO.setPlantID(plantInventoryItemDTO.get_id()); //maintenanceRequest.getPlant().getId();
        MaintenanceOrderDTO maintenanceOrderDTO2 = restTemplate.postForObject(BASE_URL + "orders", maintenanceOrderDTO, MaintenanceOrderDTO.class);
        MaintenanceOrder maintenanceOrder = new MaintenanceOrder();
        System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ"+ maintenanceOrderDTO2);
        maintenanceOrder.set_xlink(new ExtendedLink(GET_MAINTENENACE + "order/"+ maintenanceOrderDTO2.getId(), "getMO", HttpMethod.GET));
        maintenanceOrderRepository.save(maintenanceOrder);
        maintenanceRequest.setMo(maintenanceOrder);
        maintenanceRequest.setStatus(MRStatus.ACCEPTED);
        maintenanceRequestRepository.save(maintenanceRequest);

        return maintenanceOrder;
    }

    public MaintenanceRequestDTO getMaintenanceRequest(Long id) {
        MaintenanceRequest mr = maintenanceRequestRepository.findById(id).orElse(null);
        MaintenanceRequestDTO maintenanceRequestDTO = new MaintenanceRequestDTO();
        maintenanceRequestDTO.set_id(mr.getId());
        maintenanceRequestDTO.setPlantID(mr.getPlant().getId());
        maintenanceRequestDTO.setExpectedPeriod(mr.getExpectedPeriod());
        maintenanceRequestDTO.setDescription(mr.getDescription());
        maintenanceRequestDTO.setConstructionSiteId(mr.getConstructionSiteId());
        maintenanceRequestDTO.setSiteEngineerName(mr.getSiteEngineerName());
        maintenanceRequestDTO.setStatus(mr.getStatus().toString());
        maintenanceRequestDTO.setMo(mr.getMo());
        return maintenanceRequestDTO;
    }

        public MaintenanceRequest cancelMaintenanceOrder(MaintenanceRequest maintenanceRequest) throws Exception {
            String href = maintenanceRequest.getMo().get_xlink().getHref();
            MaintenanceOrderDTO moDTO = getMaintenanceOrder(new ExtendedLink(href, "getMO", HttpMethod.GET));
            String remote_mo_id = moDTO.getId().toString();
            RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
            String response = template.patchForObject(BASE_URL + "order/" + remote_mo_id + "/cancel", remote_mo_id, String.class);
            System.out.println(response);
            if (response.contains("CANCELLED")) {
                maintenanceRequest.setStatus(MRStatus.CANCELLED);
                maintenanceRequestRepository.save(maintenanceRequest);
            }
            else {
                List<String> list = Stream.of("Error While Cancel the Maintenance Order").collect(Collectors.toList());
                throw new Exception(ErrorResponseHelper.stringListToJson(list));

            }
        return maintenanceRequest;
    }
}
