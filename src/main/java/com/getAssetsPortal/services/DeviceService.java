package com.getAssetsPortal.services;

import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.DeviceSwapDto;

public interface DeviceService {

    void swapDevice(DeviceSwapDto request);
    DeviceHistoryResponse getDeviceHistory(String value);
}
