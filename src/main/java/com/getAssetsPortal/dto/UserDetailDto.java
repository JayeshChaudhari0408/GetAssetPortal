package com.getAssetsPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDto {

    private String domainId;
    private String employeeCode;
    private String location;
    private String email;
    private String department;
    private String contactNumber;
}