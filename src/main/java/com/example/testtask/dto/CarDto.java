package com.example.testtask.dto;

import com.example.testtask.enums.CarType;
import com.example.testtask.utils.Messages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {

    private Long id;

    @NotNull(message = Messages.CAR_NUMBER_IS_EMPTY)
    @Pattern(regexp = "^[ABEKMHOPCTYX]\\d{3}[ABEKMHOPCTYX]{2}\\s\\d{2,3}$", message = Messages.CAR_NUMBER_IS_NOT_VALID)
    private String carNumber;

    @NotNull(message = Messages.CAR_TYPE_IS_EMPTY)
    private CarType type;

    @Null(message = Messages.CAR_DRIVER_ID_IS_PRESENT)
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
