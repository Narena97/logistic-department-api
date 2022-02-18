package com.example.logisticdepartment.service;

import com.example.logisticdepartment.dto.DriversLicenseDto;
import com.example.logisticdepartment.mapper.DriversLicenseMapper;
import com.example.logisticdepartment.repository.DriversLicenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriversLicenseService {

    private final DriversLicenseRepository driversLicenseRepository;
    private final DriversLicenseMapper driversLicenseMapper;

    public List<DriversLicenseDto> getAllLicenses() {
        List<DriversLicenseDto> licenses = driversLicenseRepository.findAll().stream()
                .map(driversLicenseMapper::driversLicenseToDriversLicenseDto)
                .collect(Collectors.toList());
        log.info("Driver's licenses were found: {}", licenses.stream().map(DriversLicenseDto::getId).collect(Collectors.toList()));
        return licenses;
    }

}
