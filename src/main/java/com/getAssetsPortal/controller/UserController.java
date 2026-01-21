package com.getAssetsPortal.controller;

import com.getAssetsPortal.dto.UserAssetDto;
import com.getAssetsPortal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/user-assets")
    public ResponseEntity<List<UserAssetDto>> getUserAssets(
            @RequestParam String value) {

        return ResponseEntity.ok(userService.getAssetsByUser(value));
    }


}
