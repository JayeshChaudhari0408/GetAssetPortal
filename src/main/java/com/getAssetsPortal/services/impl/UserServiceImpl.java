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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final DeviceRepository deviceRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    private Users resolveUser(String value) {
        return userRepository.findByEmployeeCode(value)
                .or(() -> userRepository.findByDomainId(value))
                .orElseThrow(() -> new RuntimeException("User not found: " + value));
    }

    @Override
    public UserAssetResponseDto getUserAssets(String value) {

        Users user = resolveUser(value);

        UserAssetResponseDto response = new UserAssetResponseDto();

        UserDetailDto userDetails = new UserDetailDto();
        userDetails.setDomainId(user.getDomainId());
        userDetails.setEmployeeCode(user.getEmployeeCode());
        userDetails.setLocation(user.getLocation());
        userDetails.setEmail(user.getEmail());
        userDetails.setDepartment(user.getDepartment());
        userDetails.setContactNumber(user.getContactNumber());

        response.setUserDetailDto(userDetails);

        List<DeviceAssignment> assignments = assignmentRepository.findByUsers_IdAndDeallocatedOnIsNull(user.getId());

        List<UserAssetRowDto> assets = assignments.stream().map(a -> {
            Devices d = a.getDevices();
            UserAssetRowDto row = new UserAssetRowDto();

            row.setRemark(a.getUsedBy());
            row.setAssignedDate(a.getAllocatedOn());
            row.setAssignedBy("System"); // Not captured

            row.setAssetControlledBy(d.getAssetControlledBy());
            row.setDeviceType(d.getDeviceType());
            row.setDeviceSubType(d.getDeviceSubType());
            row.setBrand(d.getBrand());
            row.setModel(d.getModelName());
            row.setSerialNumber(d.getSerialNo());
            row.setHostName(d.getHostName());
            row.setAssignedTo(user.getDomainId());
            row.setUsedBy(a.getUsedBy());
            row.setImei(d.getImei());
            row.setMacId(d.getMacId());
            row.setInstalledDate(d.getInstallDate());
            // row.setAssetCertification(d.getAssetCertification()); // Checking entity...
            // not present in Devices
            // row.setFilesUploaded(d.getFiles()); // Not present

            return row;
        }).collect(Collectors.toList());

        response.setUserAssetRowDto(assets);

        return response;
    }
}
