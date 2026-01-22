package com.getAssetsPortal.services.export;

import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.UserAssetDto;

import java.util.List;

public interface ExportService {

    byte[] exportUserAssets(List<UserAssetDto> assets);

    byte[] exportDeviceHistory(DeviceHistoryResponse response);
}