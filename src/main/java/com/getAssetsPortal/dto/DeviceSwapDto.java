package com.getAssetsPortal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSwapDto {

    @NotNull
    private String serialNo;

    private String toUserId;

    private String remark;
}
