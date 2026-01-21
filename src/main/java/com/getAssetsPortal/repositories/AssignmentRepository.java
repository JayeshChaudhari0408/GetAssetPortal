package com.getAssetsPortal.repositories;

import com.getAssetsPortal.entity.DeviceAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<DeviceAssignment,Long> {
    Optional<DeviceAssignment> findByDevices_IdAndDeallocatedOnIsNull(Long deviceId);
}
