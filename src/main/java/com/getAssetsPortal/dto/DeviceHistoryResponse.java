package com.getAssetsPortal.dto;

import com.getAssetsPortal.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceHistoryResponse {

    private DeviceDetailsDto deviceDetails;
    private List<DeviceHistoryRowDto> history;
}
