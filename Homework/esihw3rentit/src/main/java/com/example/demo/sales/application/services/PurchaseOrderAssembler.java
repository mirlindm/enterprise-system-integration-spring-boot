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
        dto.set_id(purchaseOrder.getId());
        dto.setStatus(purchaseOrder.getStatus());

        dto.setRentalPeriod( BusinessPeriodDTO.of( purchaseOrder.getRentalPeriod().getStartDate(), purchaseOrder.getRentalPeriod().getEndDate()));

        dto.setPlant(plantInventoryEntryAssembler.toModel(purchaseOrder.getPlant()));

        dto.add(linkTo(methodOn(SalesRestController.class)
                .fetchPurchaseOrder(dto.get_id())).withRel("fetch"));
        try {
            switch (purchaseOrder.getStatus()) {
                case PENDING:
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .acceptPurchaseOrder(dto.get_id())).withRel("accept")
                            .withType(HttpMethod.POST.toString()));
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .rejectPurchaseOrder(dto.get_id())).withRel("reject")
                            .withType(HttpMethod.DELETE.toString()));
                    break;
                case OPEN:
                    dto.add(linkTo(methodOn(SalesRestController.class)
                            .retrievePurchaseOrderExtensions(dto.get_id())).withRel("extend"));
                default:
                    break;
            }
        } catch (Exception e) {}

        return dto;

    }
}
