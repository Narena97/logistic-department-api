package com.example.testtask.dto;

import com.example.testtask.enums.CarType;
import lombok.Data;

@Data
public class CarDto {

    private Long id;

    private String carNumber;

    private CarType type;

    private Long driverId;

    public CarDto(String carNumber, CarType type) {
        this.carNumber = carNumber;
        this.type = type;
    }

}
