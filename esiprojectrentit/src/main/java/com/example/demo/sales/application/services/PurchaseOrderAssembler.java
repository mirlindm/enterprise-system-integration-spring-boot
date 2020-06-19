package com.example.demo.sales.application.services;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.service.PlantInventoryEntryAssembler;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.rest.SalesRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PurchaseOrderAssembler extends RepresentationModelAssemblerSupport<PurchaseOrder, PurchaseOrderDTO> {
    public PurchaseOrderAssembler(){
        super(SalesRestController.class, PurchaseOrderDTO.class);
    }

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;
    @Override
    public PurchaseOrderDTO toModel(PurchaseOrder purchaseOrder) {
        PurchaseOrderDTO dto = createModelWithId(purchaseOrder.getId(), purchaseOrder);
        dto.setId(purchaseOrder.getId());
        dto.setStatus(purchaseOrder.getStatus());

        dto.setRentalPeriod( BusinessPeriodDTO.of( purchaseOrder.getRentalPeriod().getStartDate(), purchaseOrder.getRentalPeriod().getEndDate()));

        dto.setPlantEntry(plantInventoryEntryAssembler.toModel(purchaseOrder.getPlantEntry()));

        dto.setTotal(purchaseOrder.getTotal());
        dto.setPaymentSchedule(purchaseOrder.getPaymentSchedule());
        dto.setIssueDate(purchaseOrder.getIssueDate());




        dto.add(linkTo(methodOn(SalesRestController.class)
                .fetchPurchaseOrder(dto.getId())).withRel("fetch"));

        try {
            switch (purchaseOrder.getStatus()) {
                case PENDING:
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .acceptPurchaseOrder(dto.getId())).withRel("accept")
                            .withType(HttpMethod.POST.toString()));
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .rejectPurchaseOrder(dto.getId())).withRel("reject")
                            .withType(HttpMethod.DELETE.toString()));
                    break;
                case OPEN:
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .retrievePurchaseOrderExtensions(dto.getId())).withRel("extend"));
                case ACCEPTED:
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .retrievePurchaseOrderExtensions(dto.getId())).withRel("extend"));
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .plantDispatched(dto.getId())).withRel("dispatched"));
                case PLANT_DISPATCHED:
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .retrievePurchaseOrderExtensions(dto.getId())).withRel("extend"));
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .plantDelivered(dto.getId())).withRel("delivered"));
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .plantRejected(dto.getId())).withRel("rejected"));
                case PLANT_DELIVERED:
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .retrievePurchaseOrderExtensions(dto.getId())).withRel("extend"));
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .plantReturned(dto.getId())).withRel("returned"));
                default:
                    break;
            }
        } catch (Exception e) {}

        return dto;

    }
}
