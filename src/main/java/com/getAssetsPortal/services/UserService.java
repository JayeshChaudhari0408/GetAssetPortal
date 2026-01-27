package com.getAssetsPortal.services;

import com.getAssetsPortal.dto.UserAssetResponseDto;

import java.util.List;

public interface UserService {

    UserAssetResponseDto  getUserAssets(String value);
}
