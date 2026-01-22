package com.getAssetsPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceHistoryRowDto {

    private String domainId;
    private String employeeCode;

    private LocalDateTime assignedOn;
    private LocalDateTime unassignedOn;
    private String assignedBy;
}

