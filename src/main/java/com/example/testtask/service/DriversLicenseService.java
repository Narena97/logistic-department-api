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

@Service
public class DriversLicenseService {

    private final DriversLicenseRepository driversLicenseRepository;

    @Autowired
    public DriversLicenseService(DriversLicenseRepository driversLicenseRepository,
                                 DriversLicenseMapper driversLicenseMapper) {
        this.driversLicenseRepository = driversLicenseRepository;
    }

    public boolean licenseIsValid(DriversLicenseDto driversLicenseDto, boolean isUpdate) {
        String licenseNumber = driversLicenseDto.getDriversLicenseNumber().toString();
        LocalDate currentDate = LocalDate.now();
        if (licenseNumber.length() != 10) {
            throw new ValidationException(isUpdate ?
                    String.format(Messages.UPDATE_ENTITY + Messages.NUMBER + Messages.LENGTH_SHOULD_BE, Messages.DRIVERS_LICENSE, Messages.DRIVERS_LICENSE) :
                    String.format(Messages.ADD_ENTITY + Messages.NUMBER + Messages.LENGTH_SHOULD_BE, Messages.DRIVERS_LICENSE, Messages.DRIVERS_LICENSE));
        }
        if (driversLicenseDto.getCategory() == null && !isUpdate) {
            throw new ValidationException(String.format(Messages.ADD_ENTITY + Messages.CATEGORY + Messages.SHOULD_NOT_BE_EMPTY,
                    Messages.DRIVERS_LICENSE, Messages.DRIVERS_LICENSE));
        }
        if (driversLicenseDto.getExpirationTime() == null && !isUpdate) {
            throw new ValidationException(String.format(Messages.ADD_ENTITY + Messages.EXPIRATION_TIME + Messages.SHOULD_NOT_BE_EMPTY,
                    Messages.DRIVERS_LICENSE, Messages.DRIVERS_LICENSE));
        }
        if (!driversLicenseDto.getExpirationTime().isAfter(currentDate)) {
            throw new ValidationException(isUpdate ?
                    String.format(Messages.UPDATE_ENTITY + Messages.EXPIRED, Messages.DRIVERS_LICENSE, Messages.DRIVERS_LICENSE) :
                    String.format(Messages.ADD_ENTITY + Messages.EXPIRED, Messages.DRIVERS_LICENSE, Messages.DRIVERS_LICENSE));
        }

        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        for (DriversLicense license : licenses) {
            if (license.getDriversLicenseNumber().equals(driversLicenseDto.getDriversLicenseNumber())) {
                throw new EntityExistsException(String.format(Messages.ADD_ENTITY + Messages.WITH_NUMBER + Messages.ALREADY_EXISTS,
                        Messages.DRIVERS_LICENSE, Messages.DRIVERS_LICENSE, Messages.DRIVERS_LICENSE, driversLicenseDto.getDriversLicenseNumber()));
            }
        }
        return true;
    }

}
