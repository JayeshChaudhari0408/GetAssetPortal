package com.getAssetsPortal.services;

import com.getAssetsPortal.dto.UserAssetDto;

import java.util.List;

public interface UserService {

    List<UserAssetDto> getAssetsByUser(String value);
}
