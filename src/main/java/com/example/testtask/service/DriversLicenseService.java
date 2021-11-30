package com.example.testtask.service;

import com.example.testtask.dto.DriversLicenseDto;
import com.example.testtask.entity.DriversLicense;
import com.example.testtask.enums.LicenseCategory;
import com.example.testtask.exception.DriversLicenseException;
import com.example.testtask.mapper.DriversLicenseMapper;
import com.example.testtask.repository.DriversLicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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

    /*public void saveLicense(DriversLicenseDto driversLicenseDto) {
        if (licenseIsValid(driversLicenseDto)) {
            List<DriversLicense> licenses = driversLicenseRepository.findAll();
            DriversLicense driversLicense = driversLicenseMapper.driversLicenseDtoToDriversLicense(driversLicenseDto);

            if (licenses.isEmpty()) {
                driversLicenseRepository.saveAndFlush(driversLicense);
            } else {
                for (DriversLicense license : licenses) {
                    if (license.getDriversLicenseNumber().equals(driversLicenseDto.getDriversLicenseNumber())) {
                        throw new EntityExistsException("Could not add new driver's license: driver's license with license number "
                                + driversLicense.getDriversLicenseNumber() + " already exists");
                    } else {
                        driversLicenseRepository.saveAndFlush(driversLicense);
                    }
                }
            }
        }
    }*/

    /*public void deleteLicense(Long id) {
        DriversLicense driversLicense = driversLicenseRepository.findById(id).orElseThrow(null);
        if (driversLicense != null) {
            driversLicenseRepository.deleteById(id);
        }
    }*/

    public boolean licenseIsValid(DriversLicenseDto driversLicenseDto) {
        String licenseNumber = driversLicenseDto.getDriversLicenseNumber().toString();
        LocalDate currentDate = LocalDate.now();
        if (licenseNumber.length() != 10) {
            throw new DriversLicenseException("The length of driver's license number must be 10");
        }
        if (driversLicenseDto.getCategory() == null) {
            throw new DriversLicenseException("The category of driver's license must be filled in");
        }
        if (driversLicenseDto.getExpirationTime() == null) {
            throw new DriversLicenseException("The expiration time of driver's license must be filled in");
        }
        if (!driversLicenseDto.getExpirationTime().isAfter(currentDate)) {
            throw new DriversLicenseException("Driver's license expired");
        }

        List<DriversLicense> licenses = driversLicenseRepository.findAll();
        DriversLicense driversLicense = driversLicenseMapper.driversLicenseDtoToDriversLicense(driversLicenseDto);

        for (DriversLicense license : licenses) {
            if (license.getDriversLicenseNumber().equals(driversLicenseDto.getDriversLicenseNumber())) {
                throw new EntityExistsException("Could not add new driver's license: driver's license with license number "
                        + driversLicense.getDriversLicenseNumber() + " already exists");
            }
        }
        return true;
    }

}
