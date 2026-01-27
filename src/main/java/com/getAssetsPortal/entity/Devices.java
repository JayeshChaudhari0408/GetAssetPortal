package com.getAssetsPortal.entity;

import com.getAssetsPortal.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Devices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imei")
    private String imei;

    @Column(name = "serial_no")
    private String serialNo;

    private String brand;

    @Column(name = "lot_number")
    private Long lotNumber;

    @Column(name = "mac_id")
    private String macId;

    @Column(name = "asset_code")
    private String assetCode;

    @Column(name = "host_name")
    private String hostName;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String configuration;

    @Column(name = "asset_controlled_by")
    private String assetControlledBy;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "device_sub_type")
    private String deviceSubType;

    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    @Column(name = "warranty_months")
    private Long warrantyMonths;

    @Column(name = "warranty_expiry")
    private LocalDateTime warrantyExpiry;

    @Column(name = "installed_by")
    private String installedBy;

    @Column(name = "install_date")
    private LocalDateTime installDate;

    @Column(name = "pulled_by")
    private String pulledBy;

    @Column(name = "pulled_date")
    private LocalDateTime pulledDate;

    @Column(name = "asset_cso")
    private String assetCso;

    private String modelName;

    private String assetCertification;

    private String remark;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}