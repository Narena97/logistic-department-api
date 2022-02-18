package com.example.logisticdepartment.dto;

import com.example.logisticdepartment.enums.CarType;
import com.example.logisticdepartment.utils.Messages;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO автомобиля")
public class CarDto {

    @ApiModelProperty(notes = "Уникальный id автомобиля")
    private Long id;

    @NotNull(message = Messages.CAR_NUMBER_IS_EMPTY)
    @Pattern(regexp = "^[ABEKMHOPCTYX]\\d{3}[ABEKMHOPCTYX]{2}\\s\\d{2,3}$", message = Messages.CAR_NUMBER_IS_NOT_VALID)
    @ApiModelProperty(notes = "Уникальный государственный номер автомобиля")
    private String carNumber;

    @NotNull(message = Messages.CAR_TYPE_IS_EMPTY)
    @ApiModelProperty(notes = "Тип автомобиля")
    private CarType type;

    @Null(message = Messages.CAR_DRIVER_ID_IS_PRESENT)
    @ApiModelProperty(notes = "Id водителя данного автомобиля")
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
