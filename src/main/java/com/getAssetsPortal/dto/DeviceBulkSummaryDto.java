package com.getAssetsPortal.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DeviceBulkSummaryDto {

    private int totalRows;
    private int successCount;
    private int failureCount;

    private List<DeviceBulkRowResultDto> results;
}
