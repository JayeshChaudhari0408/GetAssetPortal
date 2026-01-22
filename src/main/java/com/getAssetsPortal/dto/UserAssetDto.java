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
public class UserAssetDto {

    private String domainId;
    private String employeeCode;

    private String remark;

    private LocalDateTime assignedDate;
    private String assignedBy;

    private String assetControlledBy;

    private String deviceType;
    private String deviceSubType;

    private String brand;
    private String model;

    private String serialNumber;
    private String hostName;

    private String assignedTo;

    private String usedBy;

    private String imei;
    private String macId;

    private LocalDateTime installedDate;

    private String assetCertification;

    private String filesUploaded;
}

