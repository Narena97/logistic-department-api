package com.example.testtask;

import com.example.testtask.entity.Car;
import com.example.testtask.entity.Driver;
import com.example.testtask.entity.DriversLicense;
import com.example.testtask.enums.CarType;
import com.example.testtask.enums.LicenseCategory;
import com.example.testtask.repository.CarRepository;
import com.example.testtask.repository.DriverRepository;
import com.example.testtask.repository.DriversLicenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InitClass implements CommandLineRunner {

    private final DriverRepository driverRepository;
    private final DriversLicenseRepository driversLicenseRepository;
    private final CarRepository carRepository;

    @Override
    public void run(String... args) throws Exception {
        /*driverRepository.deleteAll();
        driversLicenseRepository.deleteAll();
        carRepository.deleteAll();

        DriversLicense license = new DriversLicense().setDriversLicenseNumber(1234567890L)
                .setCategory(LicenseCategory.B)
                .setExpirationTime(LocalDate.of(2021, 12, 12));
        DriversLicense license2 = new DriversLicense().setDriversLicenseNumber(5432167890L)
                .setCategory(LicenseCategory.C)
                .setExpirationTime(LocalDate.of(2022, 12, 12));
        DriversLicense license3 = new DriversLicense().setDriversLicenseNumber(1352425698L)
                .setCategory(LicenseCategory.B)
                .setExpirationTime(LocalDate.of(2025, 12, 25));
        DriversLicense license4 = new DriversLicense().setDriversLicenseNumber(53134214785L)
                .setCategory(LicenseCategory.C)
                .setExpirationTime(LocalDate.of(2026, 9, 5));

        Driver driver = driverRepository.save(new Driver().setLicense(license));
        Driver driver1 = driverRepository.save(new Driver().setLicense(license2));
        Driver driver2 = driverRepository.save(new Driver().setLicense(license3));
        Driver driver3 = driverRepository.save(new Driver().setLicense(license4));

        Car car = carRepository.saveAndFlush(new Car().setCarNumber("A503CH 97").setType(CarType.PASSENGER_CAR));
        Car car1 = carRepository.saveAndFlush(new Car().setCarNumber("B456MO 55").setType(CarType.TRUCK));
        Car car2 = carRepository.saveAndFlush(new Car().setCarNumber("C371XB 44").setType(CarType.BUS));
        Car car3 = carRepository.saveAndFlush(new Car().setCarNumber("H981KE 32").setType(CarType.TRUCK));

        driver.addCar(car);
        driver.addCar(car1);
        driver.addCar(car2);

        driverRepository.saveAndFlush(driver);*/

    }
}
