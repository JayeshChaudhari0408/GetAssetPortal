package com.getAssetsPortal.dto;

import com.getAssetsPortal.entity.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeviceBulkRowResultDto {

    private int rowNumber;
    private String status; // SUCCESS / FAILED
    private String errorReason;

    // Full device table output
    private Long id;
    private String imei;
    private String serialNo;
    private String brand;
    private Long lotNumber;
    private String macId;
    private String assetCode;
    private String hostName;
    private Status deviceStatus;
    private String configuration;
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
    private String assetCso;
    private String remark;
    private String modelName;

}
