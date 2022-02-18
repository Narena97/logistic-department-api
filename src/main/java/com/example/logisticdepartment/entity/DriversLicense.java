package com.example.logisticdepartment.entity;

import com.example.logisticdepartment.enums.LicenseCategory;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "drivers_license")
@AllArgsConstructor
@NoArgsConstructor
public class DriversLicense {

    @Id
    @Column(name = "drivers_license_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
