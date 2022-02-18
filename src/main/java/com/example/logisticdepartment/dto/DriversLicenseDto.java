package com.example.logisticdepartment.dto;

import com.example.logisticdepartment.enums.LicenseCategory;
import com.example.logisticdepartment.utils.Messages;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@ApiModel(description = "DTO водительских прав")
public class DriversLicenseDto {

    @ApiModelProperty(notes = "Уникальный id водительских прав")
    private Long id;

    @NotNull(message = Messages.DRIVERS_LICENSE_NUMBER_IS_EMPTY)
    @Min(value = 1000000000, message = Messages.DRIVERS_LICENSE_NUMBER_IS_NOT_VALID)
    @DecimalMax(value = "9999999999", message = Messages.DRIVERS_LICENSE_NUMBER_IS_NOT_VALID)
    @ApiModelProperty(notes = "Уникальный номер водительских прав")
    private Long driversLicenseNumber;

    @NotNull(message = Messages.DRIVERS_LICENSE_CATEGORY_IS_EMPTY)
    @ApiModelProperty(notes = "Категория водительских прав")
    private LicenseCategory category;

    @NotNull(message = Messages.DRIVERS_LICENSE_EXPIRATION_TIME_IS_EMPTY)
    @FutureOrPresent(message = Messages.DRIVERS_LICENSE_EXPIRED)
    @ApiModelProperty(notes = "Срок действия водительских прав")
    private LocalDate expirationTime;

    public DriversLicenseDto(Long driversLicenseNumber,
                             LicenseCategory category,
                             LocalDate expirationTime) {
        this.driversLicenseNumber = driversLicenseNumber;
        this.category = category;
        this.expirationTime = expirationTime;
    }

}
