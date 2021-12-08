package com.example.testtask.mapper;

import com.example.testtask.dto.DriversLicenseDto;
import com.example.testtask.entity.DriversLicense;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface DriversLicenseMapper {

    DriversLicenseDto driversLicenseToDriversLicenseDto(DriversLicense driversLicense);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDriversLicenseFromDto(DriversLicenseDto driversLicenseDto, @MappingTarget DriversLicense driversLicense);

}
