package com.example.testtask.service;

import com.example.testtask.dto.DriversLicenseDto;
import com.example.testtask.mapper.DriversLicenseMapper;
import com.example.testtask.repository.DriversLicenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DriversLicenseService {

    private final DriversLicenseRepository driversLicenseRepository;
    private final DriversLicenseMapper driversLicenseMapper;

    @Autowired
    public DriversLicenseService(DriversLicenseRepository driversLicenseRepository,
                                 DriversLicenseMapper driversLicenseMapper) {
        this.driversLicenseRepository = driversLicenseRepository;
        this.driversLicenseMapper = driversLicenseMapper;
    }

    public List<DriversLicenseDto> getAllLicenses() {
        List<DriversLicenseDto> licenses = driversLicenseRepository.findAll().stream()
                .map(driversLicenseMapper::driversLicenseToDriversLicenseDto)
                .collect(Collectors.toList());
        log.info("Driver's licenses was found: {}", licenses);
        return licenses;
    }

}
