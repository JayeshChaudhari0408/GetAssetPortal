package com.getAssetsPortal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte profilePicture;

    @NotNull
    private String domainId;

    @NotNull
    private Long employeeCode;

    private String L1Manager;
}
