package com.example.testtask.dto;

import com.example.testtask.utils.Messages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDto {

    private Long id;

    @Valid
    @NotNull(message = Messages.DRIVER_LICENSE_IS_EMPTY)
    private DriversLicenseDto license;

    private List<CarDto> cars;

    public DriverDto(DriversLicenseDto license) {
        this.license = license;
    }

}
