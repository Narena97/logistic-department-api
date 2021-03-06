package com.example.logisticdepartment.mapper;

import com.example.logisticdepartment.dto.CarDto;
import com.example.logisticdepartment.entity.Car;
import org.mapstruct.*;

@Mapper
public interface CarMapper {

    @Mappings({
            @Mapping(source = "car.driver.id", target = "driverId")
    })
    CarDto carToCarDto(Car car);

    /*@Mappings({
            @Mapping(source = "carDto.driverId", target = "driver.id")
    })*/
    Car carDtoToCar(CarDto carDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCarFromDto(CarDto carDto, @MappingTarget Car car);

}
