package com.example.testtask.dto;

import com.example.testtask.enums.LicenseCategory;
import com.example.testtask.utils.Messages;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class DriversLicenseDto {

    private Long id;

    @NotNull(message = Messages.DRIVERS_LICENSE_NUMBER_IS_EMPTY)
    @Min(value = 1000000000, message = Messages.DRIVERS_LICENSE_NUMBER_IS_NOT_VALID)
    @DecimalMax(value = "9999999999", message = Messages.DRIVERS_LICENSE_NUMBER_IS_NOT_VALID)
    private Long driversLicenseNumber;

    @NotNull(message = Messages.DRIVERS_LICENSE_CATEGORY_IS_EMPTY)
    private LicenseCategory category;

    @NotNull(message = Messages.DRIVERS_LICENSE_EXPIRATION_TIME_IS_EMPTY)
    @FutureOrPresent(message = Messages.DRIVERS_LICENSE_EXPIRED)
    private LocalDate expirationTime;

    public DriversLicenseDto(Long driversLicenseNumber,
                             LicenseCategory category,
                             LocalDate expirationTime) {
        this.driversLicenseNumber = driversLicenseNumber;
        this.category = category;
        this.expirationTime = expirationTime;
    }

}
