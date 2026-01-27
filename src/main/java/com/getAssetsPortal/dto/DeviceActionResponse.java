package com.getAssetsPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceActionResponse {
    private String serialNo;
    private String action;
    private String assignedTo;
    private LocalDateTime timestamp;
}