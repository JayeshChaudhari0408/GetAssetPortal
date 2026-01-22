package com.getAssetsPortal.services.impl;

import com.getAssetsPortal.dto.*;
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
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;


    private Users resolveUser(String value) {
        return userRepository.findByEmployeeCode(value)
                .or(() -> userRepository.findByDomainId(value))
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + value));
    }

    // =====================================================
    // CORE OPERATIONS
    // =====================================================

    private void swap(Devices device, Users newUser, String remark) {

        DeviceAssignment current =
                assignmentRepository
                        .findByDevices_IdAndDeallocatedOnIsNull(device.getId())
                        .orElseThrow(() ->
                                new RuntimeException("No active assignment found"));

        current.setDeallocatedOn(LocalDateTime.now());
        assignmentRepository.save(current);

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
                        .orElseThrow(() ->
                                new RuntimeException("Device not active"));

        current.setDeallocatedOn(LocalDateTime.now());
        assignmentRepository.save(current);

        device.setStatus(Status.PULLED);
    }

    private void assign(Devices device, Users user, String remark) {

        DeviceAssignment assignment = new DeviceAssignment();
        assignment.setDevices(device);
        assignment.setUsers(user);
        assignment.setAllocatedOn(LocalDateTime.now());
        assignment.setUsedBy(remark);

        assignmentRepository.save(assignment);

        device.setStatus(Status.ACTIVE);
    }

    // =====================================================
    // SWAP / ASSIGN / PULL API
    // =====================================================
    @Override
    @Transactional
    public void swapDevice(DeviceSwapDto request) {

        Devices device = deviceRepository
                .findBySerialNoIgnoreCase(request.getSerialNo().trim())
                .orElseThrow(() ->
                        new RuntimeException("Device not found: " + request.getSerialNo()));

        Status status = device.getStatus();
        String assignTo = request.getToUserId();

        // ACTIVE → ACTIVE
        if (status == Status.ACTIVE && assignTo != null) {
            Users newUser = resolveUser(assignTo);
            swap(device, newUser, request.getRemark());
            return;
        }

        // ACTIVE → PULLED
        if (status == Status.ACTIVE && assignTo == null) {
            pull(device, request.getRemark());
            return;
        }

        // PULLED → ACTIVE
        if (status == Status.PULLED && assignTo != null) {
            Users user = resolveUser(assignTo);
            assign(device, user, request.getRemark());
            return;
        }
        throw new RuntimeException("Invalid device state transition");
    }
    //HISTORY
    public DeviceHistoryResponse getDeviceHistory(String value) {

        Devices device = deviceRepository.findBySerialNoIgnoreCase(value)
                .or(() -> deviceRepository.findByImei(value))
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Optional<DeviceAssignment> activeAssignment =
                assignmentRepository.findByDevices_IdAndDeallocatedOnIsNull(device.getId());

        List<DeviceAssignment> assignments =
                assignmentRepository.findByDevices_IdOrderByAllocatedOnAsc(device.getId());

        DeviceDetailsDto deviceDto = new DeviceDetailsDto();
        deviceDto.setSerialNo(device.getSerialNo());
        deviceDto.setLotNumber(device.getLotNumber());
        deviceDto.setImei(device.getImei());
        deviceDto.setMacId(device.getMacId());
        deviceDto.setAssetCode(device.getAssetCode());
        deviceDto.setHostName(device.getHostName());
        deviceDto.setConfiguration(device.getConfiguration());
        deviceDto.setStatus(device.getStatus());
        deviceDto.setAssetControlledBy(device.getAssetControlledBy());
        deviceDto.setDeviceType(device.getDeviceType());
        deviceDto.setDeviceSubType(device.getDeviceSubType());
        deviceDto.setPurchaseOrderNumber(device.getPurchaseOrderNumber());
        deviceDto.setWarrantyMonths(device.getWarrantyMonths());
        deviceDto.setWarrantyExpiry(device.getWarrantyExpiry());
        deviceDto.setInstalledBy(device.getInstalledBy());
        deviceDto.setInstallDate(device.getInstallDate());
        deviceDto.setPulledBy(device.getPulledBy());
        deviceDto.setPulledDate(device.getPulledDate());
        deviceDto.setAssetCso(device.getAssetCso());

        activeAssignment.ifPresent(a ->
                deviceDto.setAssignedTo(a.getUsers().getEmployeeCode())
        );

        List<DeviceHistoryRowDto> history = assignments.stream()
                .map(a -> {
                    DeviceHistoryRowDto h = new DeviceHistoryRowDto();
                    h.setDomainId(a.getUsers().getDomainId());
                    h.setEmployeeCode(a.getUsers().getEmployeeCode());
                    h.setAssignedOn(a.getAllocatedOn());
                    h.setUnassignedOn(a.getDeallocatedOn());
                    h.setAssignedBy(a.getUsedBy());
                    return h;
                })
                .toList();

        DeviceHistoryResponse response = new DeviceHistoryResponse();
        response.setDeviceDetails(deviceDto);
        response.setHistory(history);

        return response;
    }

    @Override
    public DeviceBulkSummaryDto processCSV(MultipartFile file) {

        List<DeviceBulkRowResultDto> results = new ArrayList<>();
        int success = 0;
        int failure = 0;
        int rowNumber = 1;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            br.readLine(); // skip header

            String line;
            while ((line = br.readLine()) != null) {
                DeviceBulkRowResultDto result = processRow(line, rowNumber++);
                results.add(result);

                if ("SUCCESS".equals(result.getStatus())) {
                    success++;
                } else {
                    failure++;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV processing failed", e);
        }

        return DeviceBulkSummaryDto.builder()
                .totalRows(results.size())
                .successCount(success)
                .failureCount(failure)
                .results(results)
                .build();
    }

    private DeviceBulkRowResultDto processRow(String line, int rowNumber) {

        String[] data = line.split(",");

        try {
            if (data.length < 4) {
                throw new RuntimeException("Mandatory fields missing");
            }

            String serialNo = data[0].trim();
            String assignedTo = data[1].trim();
            String usedBy = data[2].trim();
            String deviceType = data[3].trim();

            Devices device = deviceRepository.findBySerialNo(serialNo)
                    .orElseThrow(() -> new RuntimeException("Device not found"));

            Users assignedUser = userRepository.findByDomainId(assignedTo)
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));

            userRepository.findByDomainId(usedBy)
                    .orElseThrow(() -> new RuntimeException("UsedBy user not found"));

            if (!device.getDeviceType().equalsIgnoreCase(deviceType)) {
                throw new RuntimeException("Device type mismatch");
            }

            if (device.getStatus() != Status.PULLED) {
                throw new RuntimeException("Device not available");
            }

            // Assign device
            device.setStatus(Status.ACTIVE);
            device.setInstalledBy(assignedUser.getDomainId());
            device.setInstallDate(LocalDateTime.now());

            deviceRepository.save(device);

            return buildResponse(device, rowNumber, "SUCCESS", null);

        } catch (Exception ex) {
            return DeviceBulkRowResultDto.builder()
                    .rowNumber(rowNumber)
                    .status("FAILED")
                    .errorReason(ex.getMessage())
                    .build();
        }
    }

    private DeviceBulkRowResultDto buildResponse(
            Devices d, int row, String status, String error) {

        return DeviceBulkRowResultDto.builder()
                .rowNumber(row)
                .status(status)
                .errorReason(error)
                .id(d.getId())
                .imei(d.getImei())
                .serialNo(d.getSerialNo())
                .brand(d.getBrand())
                .lotNumber(d.getLotNumber())
                .macId(d.getMacId())
                .assetCode(d.getAssetCode())
                .hostName(d.getHostName())
                .deviceStatus(d.getStatus())
                .configuration(d.getConfiguration())
                .assetControlledBy(d.getAssetControlledBy())
                .deviceType(d.getDeviceType())
                .deviceSubType(d.getDeviceSubType())
                .purchaseOrderNumber(d.getPurchaseOrderNumber())
                .warrantyMonths(d.getWarrantyMonths())
                .warrantyExpiry(d.getWarrantyExpiry())
                .installedBy(d.getInstalledBy())
                .installDate(d.getInstallDate())
                .pulledBy(d.getPulledBy())
                .pulledDate(d.getPulledDate())
                .assetCso(d.getAssetCso())
                .remark(d.getRemark())
                .modelName(d.getModelName())
                .build();
    }

}
