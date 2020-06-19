package com.example.demo.maintenance.application.dto;


import com.example.demo.inventory.domain.model.EquipmentCondition;
import com.example.demo.maintenance.domain.model.TypeOfWork;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class TowEqCondition extends RepresentationModel<TowEqCondition> {
    TypeOfWork typeOfWork;
    EquipmentCondition equipmentCondition;
}