package com.example.logisticdepartment.mapper;

import com.example.logisticdepartment.dto.DriverDto;
import com.example.logisticdepartment.dto.DriverWithCarsDto;
import com.example.logisticdepartment.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DriverMapper {

    DriverDto driverToDriverDto(Driver driver);

    Driver driverDtoToDriver(DriverDto driverDto);

    @Mapping(source = "driver", target = "driverDto")
    DriverWithCarsDto driverToDriverWithCarsDto(Driver driver);

}
