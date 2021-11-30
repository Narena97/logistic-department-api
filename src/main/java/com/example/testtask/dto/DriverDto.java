package com.example.testtask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDto {

    private Long id;

    private DriversLicenseDto license;

    private List<CarDto> cars;

    public DriverDto(DriversLicenseDto license) {
        this.license = license;
    }

}
