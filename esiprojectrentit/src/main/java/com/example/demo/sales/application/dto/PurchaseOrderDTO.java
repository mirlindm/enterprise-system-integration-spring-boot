package com.example.demo.sales.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantReservationDTO;
import com.example.demo.inventory.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Data
public class PurchaseOrderDTO extends RepresentationModel<PurchaseOrderDTO> {
    Long id;
    List<PlantReservationDTO> reservations;
    PlantInventoryEntryDTO plantEntry;
    LocalDate issueDate;
    LocalDate paymentSchedule;
    BigDecimal total;
    POStatus status;
    BusinessPeriodDTO rentalPeriod;
    String customerCompany;

    public PurchaseOrder toPurchaseOrder(PlantInventoryEntry entry, BusinessPeriod period, BigDecimal totalPrice, String customerCompany){
    PurchaseOrderDTO poDto = new PurchaseOrderDTO();
    poDto.setCustomerCompany(customerCompany);
    poDto.setTotal(totalPrice);
    return PurchaseOrder.of(entry,period,poDto);
    }

    public PurchaseOrder rejectUnavailablePlantItemPO(PlantInventoryEntry entry, BusinessPeriod period){
        PurchaseOrderDTO poDto = new PurchaseOrderDTO();
        poDto.setStatus(POStatus.REJECTED);
        return PurchaseOrder.unavailableRejection(entry,period,poDto);
    }

}
