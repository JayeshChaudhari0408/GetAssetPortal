package com.getAssetsPortal.services.impl;

import com.getAssetsPortal.dto.UserAssetResponseDto;
import com.getAssetsPortal.dto.UserAssetRowDto;
import com.getAssetsPortal.dto.UserDetailDto;
import com.getAssetsPortal.entity.DeviceAssignment;
import com.getAssetsPortal.entity.Devices;
import com.getAssetsPortal.entity.Users;
import com.getAssetsPortal.repositories.AssignmentRepository;
import com.getAssetsPortal.repositories.DeviceRepository;
import com.getAssetsPortal.repositories.UserRepository;
import com.getAssetsPortal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final DeviceRepository deviceRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    @Override
    public UserAssetResponseDto getAssetsByUser(String value) {

        Users user = userRepository.findByEmployeeCode(value)
                .or(() -> userRepository.findByDomainId(value))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ----------------------------
        // USER DETAILS (ONCE)
        // ----------------------------
        UserDetailDto userDetailDto = new UserDetailDto(
                user.getDomainId(),
                user.getEmployeeCode(),
                user.getLocation(),
                user.getEmail(),
                user.getDepartment(),
                user.getContactNumber()
        );

        // ----------------------------
        // ACTIVE ASSETS
        // ----------------------------
        List<DeviceAssignment> assignments =
                assignmentRepository
                        .findByUsers_IdAndDeallocatedOnIsNull(user.getId());

        List<UserAssetRowDto> assetRows = assignments.stream()
                .map(a -> {
                    Devices d = a.getDevices();

                    return new UserAssetRowDto(
                            d.getRemark(),
                            a.getAllocatedOn(),
                            a.getUsedBy(),
                            d.getAssetControlledBy(),
                            d.getDeviceType(),
                            d.getDeviceSubType(),
                            d.getBrand(),
                            d.getModelName(),
                            d.getSerialNo(),
                            d.getHostName(),
                            user.getEmployeeCode(),
                            a.getUsedBy(),
                            d.getImei(),
                            d.getMacId(),
                            d.getInstallDate(),
                            d.getAssetCertification(),
                            null
                    );
                })
                .toList();

        // ----------------------------
        // FINAL RESPONSE
        // ----------------------------
        return new UserAssetResponseDto(
                userDetailDto,
                assetRows
        );
    }

}
