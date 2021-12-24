package com.example.testtask.entity;

import com.example.testtask.enums.LicenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "drivers_license")
@AllArgsConstructor
@NoArgsConstructor
public class DriversLicense {

    @Id
    @Column(name = "drivers_license_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "drivers_license_number", unique = true, nullable = false)
    private Long driversLicenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private LicenseCategory category;

    @Column(name = "expiration_time", nullable = false)
    private LocalDate expirationTime;

    public DriversLicense(Long driversLicenseNumber,
                          LicenseCategory category,
                          LocalDate expirationTime) {
        this.driversLicenseNumber = driversLicenseNumber;
        this.category = category;
        this.expirationTime = expirationTime;
    }

}
