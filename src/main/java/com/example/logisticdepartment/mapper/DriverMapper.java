package com.example.logisticdepartment.mapper;

import com.example.logisticdepartment.dto.DriverDto;
import com.example.logisticdepartment.entity.Driver;
import org.mapstruct.Mapper;

@Mapper
public interface DriverMapper {

    DriverDto driverToDriverDto(Driver driver);

    Driver driverDtoToDriver(DriverDto driverDto);

}
