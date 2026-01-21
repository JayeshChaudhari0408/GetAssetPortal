package com.getAssetsPortal.services.impl;

import com.getAssetsPortal.dto.DeviceSwapDto;
import com.getAssetsPortal.entity.DeviceAssignment;
import com.getAssetsPortal.entity.Devices;
import com.getAssetsPortal.entity.Users;
import com.getAssetsPortal.entity.enums.Status;
import com.getAssetsPortal.repositories.AssignmentRepository;
import com.getAssetsPortal.repositories.DeviceRepository;
import com.getAssetsPortal.repositories.UserRepository;
import com.getAssetsPortal.services.DeviceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    private void swap(Devices device, Long newUserId, String remark) {

        DeviceAssignment current =
                assignmentRepository
                        .findByDevices_IdAndDeallocatedOnIsNull(device.getId())
                        .orElseThrow(() -> new RuntimeException("No active assignment"));

        Users newUser = userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        current.setDeallocatedOn(LocalDateTime.now());

        DeviceAssignment next = new DeviceAssignment();
        next.setDevices(device);
        next.setUsers(newUser);
        next.setAllocatedOn(LocalDateTime.now());
        next.setUsedBy(remark);

        assignmentRepository.save(next);

        device.setStatus(Status.ACTIVE);
    }

    private void pull(Devices device, String remark) {

        DeviceAssignment current =
                assignmentRepository
                        .findByDevices_IdAndDeallocatedOnIsNull(device.getId())
                        .orElseThrow(() -> new RuntimeException("Device not active"));

        current.setDeallocatedOn(LocalDateTime.now());
        device.setStatus(Status.PULLED);
    }

    private void assign(Devices device, Long userId, String remark) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DeviceAssignment assignment = new DeviceAssignment();
        assignment.setDevices(device);
        assignment.setUsers(user);
        assignment.setAllocatedOn(LocalDateTime.now());
        assignment.setUsedBy(remark);

        assignmentRepository.save(assignment);

        device.setStatus(Status.ACTIVE);
    }



    @Override
    @Transactional
    public void swapDevice(DeviceSwapDto request) {

        Devices devices = deviceRepository
                .findBySerialNoIgnoreCase(request.getSerialNo().trim())
                .orElseThrow(() -> new RuntimeException("Device Not Found"+request.getSerialNo()));

        Status currentStatus = devices.getStatus();
        Long userId = request.getToUserId();

        if (currentStatus == Status.ACTIVE && userId != null) {
            swap(devices, userId, request.getRemark());
            return;
        }

        if (currentStatus == Status.ACTIVE && userId == null) {
            pull(devices, request.getRemark());
            return;
        }

        if (currentStatus == Status.PULLED && userId != null) {
            assign(devices, userId, request.getRemark());
            return;
        }

        DeviceAssignment currentAssignment = assignmentRepository.findByDevices_IdAndDeallocatedOnIsNull(devices.getId())
                .orElseThrow(() -> new RuntimeException("Device not currently assigned"));

        Users newUser = userRepository.findById(request.getToUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentAssignment.setDeallocatedOn(LocalDateTime.now());

        DeviceAssignment newAssignment = new DeviceAssignment();
        newAssignment.setDevices(devices);
        newAssignment.setUsers(newUser);
        newAssignment.setAllocatedOn(LocalDateTime.now());
        newAssignment.setUsedBy(request.getRemark());

        assignmentRepository.save(newAssignment);

        devices.setStatus(Status.ACTIVE);
    }
}
