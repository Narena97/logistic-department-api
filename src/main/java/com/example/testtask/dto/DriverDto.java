package com.example.testtask.dto;

import com.example.testtask.utils.Messages;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "DTO водителя")
public class DriverDto {

    @ApiModelProperty(notes = "Уникальный id водителя")
    private Long id;

    @Valid
    @NotNull(message = Messages.DRIVER_LICENSE_IS_EMPTY)
    @ApiModelProperty(notes = "Водительское удостверение водителя")
    private DriversLicenseDto license;

    @ApiModelProperty(notes = "Список автомобилей, закрепленных за водителем")
    private List<CarDto> cars;

    public DriverDto(DriversLicenseDto license) {
        this.license = license;
    }

}
