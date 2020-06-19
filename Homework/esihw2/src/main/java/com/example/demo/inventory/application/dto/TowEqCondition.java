package com.example.demo.inventory.application.dto;


import com.example.demo.common.rest.ResourceSupport;
import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.inventory.domain.model.TypeOfWork;
import lombok.Data;

@Data
public class TowEqCondition extends ResourceSupport {
    TypeOfWork typeOfWork;
    EquipmentCondition equipmentCondition;
}
