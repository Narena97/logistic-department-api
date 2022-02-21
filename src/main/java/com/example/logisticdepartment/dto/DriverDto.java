package com.example.logisticdepartment.dto;

import com.example.logisticdepartment.utils.Messages;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

    public DriverDto(DriversLicenseDto license) {
        this.license = license;
    }

}
