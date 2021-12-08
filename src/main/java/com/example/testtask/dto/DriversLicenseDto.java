package com.example.testtask.dto;

import com.example.testtask.enums.LicenseCategory;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DriversLicenseDto {

    private Long id;

    private Long driversLicenseNumber;

    private LicenseCategory category;

    private LocalDate expirationTime;

    public DriversLicenseDto(Long driversLicenseNumber,
                             LicenseCategory category,
                             LocalDate expirationTime) {
        this.driversLicenseNumber = driversLicenseNumber;
        this.category = category;
        this.expirationTime = expirationTime;
    }

}
