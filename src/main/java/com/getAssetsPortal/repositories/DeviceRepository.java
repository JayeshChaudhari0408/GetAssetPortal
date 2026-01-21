package com.getAssetsPortal.repositories;

import com.getAssetsPortal.entity.Devices;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceRepository extends JpaRepository<Devices,Long> {
}
