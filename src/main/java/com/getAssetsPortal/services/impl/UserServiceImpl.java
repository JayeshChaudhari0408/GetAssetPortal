package com.getAssetsPortal.services.impl;

import com.getAssetsPortal.dto.UserAssetDto;
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
    public List<UserAssetDto> getAssetsByUser(String value) {
        Users user = userRepository.findByEmployeeCode(value)
                .or(() -> userRepository.findByDomainId(value))
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<DeviceAssignment> assignments =
                assignmentRepository
                        .findByUsers_IdAndDeallocatedOnIsNull(user.getId());

        return assignments.stream()
                .map(a -> {
                    Devices d = a.getDevices();
                    UserAssetDto dto = new UserAssetDto();
                    dto.setDomainId(user.getDomainId());
                    dto.setEmployeeCode(user.getEmployeeCode());

                    dto.setRemark(d.getRemark());

                    dto.setAssignedDate(a.getAllocatedOn());
                    dto.setAssignedBy(a.getUsedBy()); // or future field

                    dto.setAssetControlledBy(d.getAssetControlledBy());
                    dto.setDeviceType(d.getDeviceType());
                    dto.setDeviceSubType(d.getDeviceSubType());

                    dto.setBrand(d.getBrand());
                    dto.setModel(d.getModelName());
                    dto.setSerialNumber(d.getSerialNo());
                    dto.setHostName(d.getHostName());

                    dto.setAssignedTo(user.getEmployeeCode());
                    dto.setUsedBy(a.getUsedBy());

                    dto.setImei(d.getImei());
                    dto.setMacId(d.getMacId());

                    dto.setInstalledDate(d.getInstallDate());

                    dto.setAssetCertification(d.getAssetCertification());
                    dto.setFilesUploaded(null);        // not in DB
                    return dto;
                })
                .toList();
    }
}
