package com.example.testtask.mapper;

import com.example.testtask.dto.DriverDto;
import com.example.testtask.entity.Driver;
import org.mapstruct.Mapper;

@Mapper
public interface DriverMapper {

    DriverDto driverToDriverDto(Driver driver);

    Driver driverDtoToDriver(DriverDto driverDto);

}
