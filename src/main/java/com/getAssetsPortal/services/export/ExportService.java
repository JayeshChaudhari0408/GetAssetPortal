package com.getAssetsPortal.services.export;

import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.UserAssetRowDto;

import java.util.List;

public interface ExportService {

    byte[] exportUserAssets(List<UserAssetRowDto> assets);

    byte[] exportDeviceHistory(DeviceHistoryResponse response);
}