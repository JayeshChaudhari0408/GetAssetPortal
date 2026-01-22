package com.getAssetsPortal.dto;

import com.getAssetsPortal.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDetailsDto {

    private String serialNo;
    private Long lotNumber;
    private String imei;
    private String macId;
    private String assetCode;
    private String hostName;
    private String configuration;

    private String assignedTo;
    private Status status;
    private String assetControlledBy;
    private String deviceType;
    private String deviceSubType;

    private String purchaseOrderNumber;
    private Long warrantyMonths;
    private LocalDateTime warrantyExpiry;

    private String installedBy;
    private LocalDateTime installDate;

    private String pulledBy;
    private LocalDateTime pulledDate;

    private String usedBy;
    private String assetCso;
}
