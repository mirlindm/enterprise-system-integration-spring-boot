package com.example.demo.controllers.models;

import com.example.demo.maintenance.application.dto.MaintenanceTaskDTO;
import com.example.demo.maintenance.domain.model.MaintenanceTaskRequest;
import lombok.Data;

@Data
public class MaintenanceTaskFormDTO {
    MaintenanceTaskRequest maintenanceTaskRequest;
    Long plantItemId;
}
