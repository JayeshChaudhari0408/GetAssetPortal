package com.getAssetsPortal.controller;

import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.DeviceSwapDto;
import com.getAssetsPortal.services.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

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


}
