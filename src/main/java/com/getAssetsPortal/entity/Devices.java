package com.getAssetsPortal.entity;

import com.getAssetsPortal.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String Brand;
    private Long Lot_Number;
    private String MAC_ID;
    private String Asset_Code;
    private String Host_Name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    private String Configuration;
    private String Asset_Controlled_By;
    private String Device_Type;
    private String Device_Sub_Type;
    private String Purchase_Order_Number;
    private Long Warranty_months;
    private LocalDateTime Warranty_Expiry;
    private String Installed_By;
    private LocalDateTime Install_Date;
    private String Pulled_By;
    private LocalDateTime Pulled_Date;
    private String Asset_CSO;
    private String Remark;
    private String Model_name;
}
