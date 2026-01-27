package com.getAssetsPortal.repositories;

import com.getAssetsPortal.entity.DeviceAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<DeviceAssignment,Long> {

    Optional<DeviceAssignment> findByDevices_IdAndDeallocatedOnIsNull(Long deviceId);
    List<DeviceAssignment> findAllByDevices_IdInAndDeallocatedOnIsNull(List<Long> deviceIds);
    List<DeviceAssignment> findByDevices_IdOrderByAllocatedOnAsc(Long deviceId);
    List<DeviceAssignment> findByUsers_IdAndDeallocatedOnIsNull(Long userId);

}
