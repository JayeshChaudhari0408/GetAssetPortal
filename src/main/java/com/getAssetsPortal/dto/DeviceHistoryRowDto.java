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

    private String serialNumber;
    private String imei;
    private String assetCode;

    private String domainId;
    private String employeeCode;
    private String assignedTo;
    private String usedBy;

    private String action; // ASSIGNED / SWAPPED / RETURNED
    private String remark;

    private LocalDateTime actionDate;
    private String actionBy;
}

