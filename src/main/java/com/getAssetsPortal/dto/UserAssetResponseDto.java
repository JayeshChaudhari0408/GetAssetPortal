package com.getAssetsPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAssetResponseDto {

    private UserDetailDto userDetailDto;
    private List<UserAssetRowDto> userAssetRowDto;
}

