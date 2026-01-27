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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

//    private void pull(Devices device, String remark) {
//
//        DeviceAssignment current =
//                assignmentRepository
//                        .findByDevices_IdAndDeallocatedOnIsNull(device.getId())
//                        .orElseThrow(() ->
//                                new RuntimeException("Device not active"));
//
//        current.setDeallocatedOn(LocalDateTime.now());
//        assignmentRepository.save(current);
//
//        device.setStatus(Status.PULLED);
//    }

//    private void assign(Devices device, Users user, String remark) {
//
//        DeviceAssignment assignment = new DeviceAssignment();
//        assignment.setDevices(device);
//        assignment.setUsers(user);
//        assignment.setAllocatedOn(LocalDateTime.now());
//        assignment.setUsedBy(remark);
//
//        assignmentRepository.save(assignment);
//
//        device.setStatus(Status.ACTIVE);
//    }

    // =====================================================
    // SWAP / ASSIGN / PULL API
    // =====================================================
    @Override
    @Transactional
    public DeviceActionResponse swapDevice(DeviceSwapDto request) {

        Devices device = deviceRepository
                .findBySerialNoIgnoreCase(request.getSerialNo().trim())
                .orElseThrow(() ->
                        new RuntimeException("Device not found: " + request.getSerialNo()));

        Status status = device.getStatus();
        String assignTo = request.getToUserId();

        LocalDateTime actionTime = LocalDateTime.now();

        Users targetUser = assignTo != null ? resolveUser(assignTo) : null;

        if (assignTo != null && assignTo.trim().isEmpty()) {
            throw new RuntimeException("Assignee cannot be empty");
        }

        Optional<DeviceAssignment> activeAssignment =
                assignmentRepository.findByDevices_IdAndDeallocatedOnIsNull(device.getId());


        if (activeAssignment.isPresent() && targetUser != null) {

            Users currentUser = activeAssignment.get().getUsers();

            if (currentUser.getId().equals(targetUser.getId())) {
                return new DeviceActionResponse(
                        device.getSerialNo(),
                        "ALREADY_ASSIGNED",
                        currentUser.getEmployeeCode(),
                        actionTime
                );
            }
        }



        // ACTIVE → ACTIVE
        if (status == Status.ACTIVE && assignTo != null) {
            Users newUser = resolveUser(assignTo);
            swap(device, newUser, request.getRemark());
            return new DeviceActionResponse(
                    device.getSerialNo(),
                    "SWAPPED",
                    newUser.getEmployeeCode(),
                    actionTime
            );
        }

        // ACTIVE → PULLED
//        if (status == Status.ACTIVE && assignTo == null) {
//            pull(device, request.getRemark());
//            return new DeviceActionResponse(
//                    device.getSerialNo(),
//                    "PULLED",
//                    null,
//                    actionTime
//            );
//        }
//
//        // PULLED → ACTIVE
//        if (status == Status.PULLED && assignTo != null) {
//            Users user = resolveUser(assignTo);
//            assign(device, user, request.getRemark());
//            return new DeviceActionResponse(
//                    device.getSerialNo(),
//                    "ASSIGNED",
//                    user.getEmployeeCode(),
//                    actionTime
//            );
//        }
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
                    h.setSerialNumber(device.getSerialNo());
                    h.setImei(device.getImei());
                    h.setAssetCode(device.getAssetCode());
                    h.setDomainId(a.getUsers().getDomainId());
                    h.setEmployeeCode(a.getUsers().getEmployeeCode());

                    // Logic for AssignedTo and UsedBy fields in history
                    // Assuming 'assignment' maps the user who has it.
                    h.setAssignedTo(a.getUsers().getDomainId());
                    h.setUsedBy(a.getUsedBy());

                    // Action logic: If it's the first assignment, maybe "ASSIGNED".
                    // If pulled (deallocated), it might be "RETURNED" or "SWAPPED" (hard to tell
                    // from single record without looking at context headers of prev/next).
                    // For now, let's keep it simple or derive from status if active.
                    h.setAction(a.getDeallocatedOn() == null ? "ACTIVE" : "RETURNED/SWAPPED");

                    h.setRemark(device.getRemark()); // Using 'UsedBy' as remark or if there's a separate remark field?
                    // Entity doesn't seem to have a separate 'remark' field on Assignment other
                    // than 'usedBy' string sometimes used as remark.
                    // Actually, 'swap' method sets 'usedBy' with remark. So consistent.

                    h.setActionDate(a.getAllocatedOn());
                    h.setActionBy(a.getUsers().getDomainId()); // Or who performed the action? Not captured in
                    // Assignment entity.

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

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            br.readLine(); // skip header

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }

            // 1. Collect all identifiers
            Set<String> serialNos = new HashSet<>();
            Set<String> domainIds = new HashSet<>();

            for (String l : lines) {
                String[] data = l.split(",");
                if (data.length >= 4) {
                    serialNos.add(data[0].trim());
                    String assignedTo = data[1].trim();
                    String usedBy = data[2].trim();
                    if (!assignedTo.isEmpty())
                        domainIds.add(assignedTo);
                    if (!usedBy.isEmpty())
                        domainIds.add(usedBy);
                }
            }

            // 2. Batch Fetch
            Map<String, Devices> deviceMap = deviceRepository.findAllBySerialNoIn(new ArrayList<>(serialNos))
                    .stream().collect(Collectors.toMap(d -> d.getSerialNo().toLowerCase(), d -> d, (d1, d2) -> d1)); // Handle
            // duplicates
            // if
            // any

            Map<String, Users> userMap = userRepository.findAllByDomainIdIn(new ArrayList<>(domainIds))
                    .stream().collect(Collectors.toMap(u -> u.getDomainId().toLowerCase(), u -> u, (u1, u2) -> u1));

            List<Long> deviceIds = deviceMap.values().stream().map(Devices::getId).collect(Collectors.toList());

            // Map<DeviceId, Assignment>
            Map<Long, DeviceAssignment> assignmentMap = assignmentRepository
                    .findAllByDevices_IdInAndDeallocatedOnIsNull(deviceIds)
                    .stream().collect(Collectors.toMap(a -> a.getDevices().getId(), a -> a, (a1, a2) -> a1));

            // 3. Process Rows in Memory
            int rowNumber = 1;
            for (String l : lines) {
                DeviceBulkRowResultDto result = processRowOptimized(l, rowNumber++, deviceMap, userMap, assignmentMap);
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

    private DeviceBulkRowResultDto processRowOptimized(String line, int rowNumber,
                                                       Map<String, Devices> deviceMap,
                                                       Map<String, Users> userMap,
                                                       Map<Long, DeviceAssignment> assignmentMap) {

        String[] data = line.split(",");

        try {
            if (data.length < 4) {
                throw new RuntimeException("Mandatory fields missing");
            }

            String serialNo = data[0].trim();
            String assignedTo = data[1].trim();
            String usedBy = data[2].trim();
            String deviceType = data[3].trim();

            Devices device = deviceMap.get(serialNo.toLowerCase());
            if (device == null) {
                throw new RuntimeException("Device not found: " + serialNo);
            }

            Users assignedUser = userMap.get(assignedTo.toLowerCase());
            if (assignedUser == null) {
                throw new RuntimeException("Assigned user not found: " + assignedTo);
            }

            // usedBy is just a string in some contexts or user? Assuming logic from
            // original processRow which fetched user
            Users usedByUser = userMap.get(usedBy.toLowerCase());
            if (usedByUser == null) {
                // In original code: userRepository.findByDomainId(usedBy).orElseThrow
                throw new RuntimeException("UsedBy user not found: " + usedBy);
            }

            // Device type normalization
            String dbType = device.getDeviceType().replaceAll("\\s+", "").toLowerCase();
            String csvType = deviceType.replaceAll("\\s+", "").toLowerCase();

            if (!dbType.equals(csvType)) {
                throw new RuntimeException("Device type mismatch");
            }

            // STATUS FIX
            if (device.getStatus() != Status.ACTIVE) {
                throw new RuntimeException("Device not active");
            }

            // === LOGIC FIX: Handle Assignment ===
            DeviceAssignment currentAssignment = assignmentMap.get(device.getId());

            // Check if actual swap is needed
            boolean assignmentChanged = false;

            if (currentAssignment != null) {
                if (!currentAssignment.getUsers().getId().equals(assignedUser.getId())) {
                    // Ends old assignment
                    currentAssignment.setDeallocatedOn(LocalDateTime.now());
                    assignmentRepository.save(currentAssignment);

                    // Creates new assignment
                    DeviceAssignment newAssignment = new DeviceAssignment();
                    newAssignment.setDevices(device);
                    newAssignment.setUsers(assignedUser);
                    newAssignment.setAllocatedOn(LocalDateTime.now());
                    newAssignment.setUsedBy(usedBy); // NOTE: Original code used 'remark' from request or 'usedBy'
                    // string?
                    // In processRow original: device.setAssetCso(usedBy);
                    // Logic in swap(): next.setUsedBy(remark);
                    // Let's use 'usedBy' string for the assignment 'usedBy' field if that's the
                    // intent.
                    // Checking original entities... DeviceAssignment has 'usedBy' field (String).

                    assignmentRepository.save(newAssignment);

                    // Update map for next occurrences if any (though unlikely in same CSV for same
                    // device)
                    assignmentMap.put(device.getId(), newAssignment);
                    assignmentChanged = true;
                }
            } else {
                // No active assignment, but device is ACTIVE per check above?
                // If device is ACTIVE but no assignment, we should create one.
                DeviceAssignment newAssignment = new DeviceAssignment();
                newAssignment.setDevices(device);
                newAssignment.setUsers(assignedUser);
                newAssignment.setAllocatedOn(LocalDateTime.now());
                newAssignment.setUsedBy(usedBy);
                assignmentRepository.save(newAssignment);
                assignmentMap.put(device.getId(), newAssignment);
                assignmentChanged = true;
            }

            // UPDATE DEVICE FIELDS
            device.setInstalledBy(assignedUser.getDomainId());
            device.setInstallDate(LocalDateTime.now());
            device.setAssetCso(usedBy); // Set assetCso to usedBy
            device.setPulledBy(null);
            device.setPulledDate(null);

            deviceRepository.save(device);

            return buildResponse(device, rowNumber, "SUCCESS", null);

        } catch (Exception ex) {
            return DeviceBulkRowResultDto.builder()
                    .rowNumber(rowNumber)
                    .serialNo(data.length > 0 ? data[0].trim() : null)
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
