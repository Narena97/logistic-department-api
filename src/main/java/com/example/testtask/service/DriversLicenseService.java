package com.example.testtask.service;

import com.example.testtask.dto.DriversLicenseDto;
import com.example.testtask.entity.DriversLicense;
import com.example.testtask.exception.ValidationException;
import com.example.testtask.mapper.DriversLicenseMapper;
import com.example.testtask.repository.DriversLicenseRepository;
import com.example.testtask.utils.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        return driversLicenseRepository.findAll().stream()
                .map(driversLicenseMapper::driversLicenseToDriversLicenseDto)
                .collect(Collectors.toList());
    }

    public boolean licenseIsValid(DriversLicenseDto driversLicenseDto, boolean isUpdate) {
        String licenseNumber = driversLicenseDto.getDriversLicenseNumber().toString();
        LocalDate currentDate = LocalDate.now();
        if (licenseNumber.length() != 10) {
            throw new ValidationException(isUpdate ? Messages.UPDATE_DRIVER_LICENSE_NUMBER_IS_NOT_VALID : Messages.ADD_DRIVER_LICENSE_NUMBER_IS_NOT_VALID);
        }
        if (driversLicenseDto.getCategory() == null && !isUpdate) {
            throw new ValidationException(Messages.ADD_DRIVER_LICENSE_CATEGORY_IS_EMPTY);
        }
        if (driversLicenseDto.getExpirationTime() == null && !isUpdate) {
            throw new ValidationException(Messages.ADD_DRIVER_LICENSE_EXPIRATION_TIME_IS_EMPTY);
        }
        if (!driversLicenseDto.getExpirationTime().isAfter(currentDate)) {
            throw new ValidationException(isUpdate ? Messages.UPDATE_DRIVER_LICENSE_EXPIRED : Messages.ADD_DRIVER_LICENSE_EXPIRED);
        }

        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        for (DriversLicense license : licenses) {
            if (license.getDriversLicenseNumber().equals(driversLicenseDto.getDriversLicenseNumber())) {
                throw new EntityExistsException(String.format(Messages.ADD_DRIVER_LICENSE_EXISTS, driversLicenseDto.getDriversLicenseNumber()));
            }
        }
        return true;
    }

}
