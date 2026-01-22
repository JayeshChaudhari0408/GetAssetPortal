package com.getAssetsPortal.services;

import com.getAssetsPortal.dto.DeviceBulkSummaryDto;
import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.DeviceSwapDto;
import org.springframework.web.multipart.MultipartFile;

public interface DeviceService {

    void swapDevice(DeviceSwapDto request);
    DeviceHistoryResponse getDeviceHistory(String value);
    DeviceBulkSummaryDto processCSV(MultipartFile file);
}
