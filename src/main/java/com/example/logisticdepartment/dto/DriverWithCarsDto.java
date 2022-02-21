package com.example.logisticdepartment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "DTO водителя с автомобилем")
public class DriverWithCarsDto {

    @ApiModelProperty(notes = "Водитель автомобиля")
    DriverDto driverDto;

    @ApiModelProperty(notes = "Список автомобилей, закрепленных за водителем")
    private List<CarDto> cars;

}
