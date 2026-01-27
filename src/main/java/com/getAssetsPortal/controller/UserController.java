package com.getAssetsPortal.controller;

import com.getAssetsPortal.dto.UserAssetResponseDto;
import com.getAssetsPortal.dto.UserAssetRowDto;
import com.getAssetsPortal.services.UserService;
import com.getAssetsPortal.services.export.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ExportService exportService;

    @GetMapping("/user-assets")
    public ResponseEntity<UserAssetResponseDto> getUserAssets(
            @RequestParam String value) {
        return ResponseEntity.ok(userService.getUserAssets(value));
    }

    @GetMapping("/user-assets/export")
    public ResponseEntity<byte[]> exportUserAssets(
            @RequestParam String value) {
        UserAssetResponseDto assetResponse = userService.getUserAssets(value);
        List<UserAssetRowDto> assets = assetResponse.getUserAssetRowDto();
        byte[] file = exportService.exportUserAssets(assets);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=user-assets.xlsx")
                .body(file);
    }
}
