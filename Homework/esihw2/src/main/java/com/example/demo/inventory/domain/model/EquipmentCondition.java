package com.example.demo.inventory.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EquipmentCondition {
    SERVICEABLE,
    UNSERVICEABLEREPAIRABLE,
    UNSERVICEABLEINCOMPLETE,
    UNSERVICEABLECONDEMNED
}
