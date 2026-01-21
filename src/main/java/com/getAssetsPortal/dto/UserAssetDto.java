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

    private String serialNo;
    private String imei;
    private String modelName;
    private Status status;
    private LocalDateTime allocatedOn;
    private String deviceType;
}

