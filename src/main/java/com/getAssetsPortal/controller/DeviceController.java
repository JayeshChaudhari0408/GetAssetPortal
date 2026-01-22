package com.getAssetsPortal.controller;

import com.getAssetsPortal.dto.DeviceBulkSummaryDto;
import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.DeviceSwapDto;
import com.getAssetsPortal.services.DeviceService;
import com.getAssetsPortal.services.export.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final ExportService exportService;

    @PutMapping("/swap")
    public ResponseEntity<String> swapDevice(@RequestBody DeviceSwapDto request) {
        deviceService.swapDevice(request);
        return ResponseEntity.ok("Assets swapped successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<DeviceHistoryResponse> history(
            @RequestParam String value) {

        return ResponseEntity.ok(deviceService.getDeviceHistory(value));
    }

    @PostMapping(value = "/bulk", consumes = "multipart/form-data")
    public ResponseEntity<DeviceBulkSummaryDto> bulkUpload(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(deviceService.processCSV(file));
    }

    @GetMapping("/history/export")
    public ResponseEntity<byte[]> exportDeviceHistory(
            @RequestParam String value) {

        DeviceHistoryResponse response =
                deviceService.getDeviceHistory(value);

        byte[] file = exportService.exportDeviceHistory(response);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=device-history.xlsx")
                .body(file);
    }


}
