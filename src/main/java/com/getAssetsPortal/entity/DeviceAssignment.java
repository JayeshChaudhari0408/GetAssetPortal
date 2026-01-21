package com.getAssetsPortal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DeviceAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Devices devices;

    private LocalDateTime allocatedOn;

    private LocalDateTime deallocatedOn;
}
