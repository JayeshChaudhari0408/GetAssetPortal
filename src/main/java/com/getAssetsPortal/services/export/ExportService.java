package com.getAssetsPortal.services.export;

import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.UserAssetResponseDto;

import java.util.List;

public interface ExportService {

    byte[] exportUserAssets(List<UserAssetResponseDto> assets);

    byte[] exportDeviceHistory(DeviceHistoryResponse response);
}