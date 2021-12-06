package com.example.testtask.dto;

import com.example.testtask.enums.CarType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {

    private Long id;

    private String carNumber;

    private CarType type;

    private Long driverId;

    public CarDto(String carNumber, CarType type) {
        this.carNumber = carNumber;
        this.type = type;
    }

    public CarDto(String carNumber, CarType type, Long driverId) {
        this.carNumber = carNumber;
        this.type = type;
        this.driverId = driverId;
    }

}
