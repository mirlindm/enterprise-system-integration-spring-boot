package com.buildit.integration;


import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.PurchaseOrder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
@MessagingGateway
public interface IntegrationService {
    @Gateway(requestChannel = "req-channel", replyChannel = "rep-channel")
    List<CollectionModel> findPlants(@Payload PlantAvailabilityRequestDTO plantAvailabilityRequestDTO);

    @Gateway(requestChannel = "req-channel-po", replyChannel = "getpo-rep-channel")
    List<CollectionModel> getPO(@Payload String custName);

    @Gateway(requestChannel = "req-channel-cancel-po", replyChannel = "can-po-rep-channel")
    List<CollectionModel> cancelPO(@Payload Long id);



}

@Service
class IntegrationServiceImpl {
    RestTemplate restTemplate = new RestTemplate();
//
//    public Object findPlants(Message<?> message) {
//        return restTemplate.getForObject("http://localhost:8088/api/v1/plant", String.class);
//    }
}
