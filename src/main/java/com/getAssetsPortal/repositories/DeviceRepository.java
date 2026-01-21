package com.getAssetsPortal.repositories;

import com.getAssetsPortal.entity.Devices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface DeviceRepository extends JpaRepository<Devices,Long> {
    @Query("""
        SELECT d FROM Devices d
        WHERE LOWER(d.serialNo) = LOWER(:serialNo)
    """)
    Optional<Devices> findBySerialNoIgnoreCase(@Param("serialNo") String serialNo);

    Optional<Devices> findBySerialNo(String serialNo);
    Optional<Devices> findByImei(String imei);


}
