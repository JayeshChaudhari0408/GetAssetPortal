package com.getAssetsPortal.services.impl;

import com.getAssetsPortal.dto.UserAssetDto;
import com.getAssetsPortal.entity.DeviceAssignment;
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
                    UserAssetDto dto = new UserAssetDto();
                    dto.setSerialNo(a.getDevices().getSerialNo());
                    dto.setImei(a.getDevices().getImei());
                    dto.setModelName(a.getDevices().getModel_name());
                    dto.setStatus(a.getDevices().getStatus());
                    dto.setAllocatedOn(a.getAllocatedOn());
                    return dto;
                })
                .toList();
    }
}
