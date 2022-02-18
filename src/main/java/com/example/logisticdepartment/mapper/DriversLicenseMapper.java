package com.example.logisticdepartment.mapper;

import com.example.logisticdepartment.dto.DriversLicenseDto;
import com.example.logisticdepartment.entity.DriversLicense;
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
