package com.example.demo.sales.application.services;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.rest.ExtendedLink;
import com.example.demo.inventory.application.service.PlantInventoryEntryAssembler;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.rest.SalesRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

@Service
public class PurchaseOrderAssembler extends ResourceAssemblerSupport<PurchaseOrder, PurchaseOrderDTO> {
    public PurchaseOrderAssembler(){
        super(SalesRestController.class, PurchaseOrderDTO.class);
    }

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;
    @Override
    public PurchaseOrderDTO toResource(PurchaseOrder purchaseOrder) {
        PurchaseOrderDTO dto = createResourceWithId(purchaseOrder.getId(), purchaseOrder);
        dto.set_id(purchaseOrder.getId());
        dto.setStatus(purchaseOrder.getStatus());

        dto.setRentalPeriod( BusinessPeriodDTO.of( purchaseOrder.getRentalPeriod().getStartDate(), purchaseOrder.getRentalPeriod().getEndDate()));

        dto.setPlant(plantInventoryEntryAssembler.toResource(purchaseOrder.getPlant()));

        try {
           switch (purchaseOrder.getStatus()) {
               case PENDING:
                   dto.add(new ExtendedLink(
                                    linkTo(methodOn(SalesRestController.class)
                            .acceptPurchaseOrder(dto.get_id().toString())).toString(),
                            "accept", POST));
                   dto.add(new ExtendedLink(
                                    linkTo(methodOn(SalesRestController.class)
                            .rejectPurchaseOrder(dto.get_id().toString())).toString(),
                            "reject", DELETE));
                   break;
               default: break;
           }
       } catch (Exception e) {}

        return dto;

    }
}
