package com.getAssetsPortal.repositories;

import com.getAssetsPortal.entity.DeviceAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<DeviceAssignment,Long> {
}
