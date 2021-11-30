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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitClass implements CommandLineRunner {

    private final DriverRepository driverRepository;
    private final DriversLicenseRepository driversLicenseRepository;
    private final CarRepository carRepository;

    @Override
    public void run(String... args) throws Exception {
        driverRepository.deleteAll();
        driversLicenseRepository.deleteAll();
        carRepository.deleteAll();

        /*DriversLicense license = driversLicenseRepository.saveAndFlush(
                new DriversLicense().setDriversLicenseNumber(12345L)
                        .setCategory(LicenseCategory.B)
                        .setExpirationTime(new Date(2021-12-12)));
        DriversLicense license2 = driversLicenseRepository.saveAndFlush(
                new DriversLicense().setDriversLicenseNumber(54321L)
                        .setCategory(LicenseCategory.C)
                        .setExpirationTime(new Date(2022-10-21)));
        DriversLicense license3 = driversLicenseRepository.saveAndFlush(
                new DriversLicense().setDriversLicenseNumber(13524L)
                        .setCategory(LicenseCategory.B)
                        .setExpirationTime(new Date(2023-11-15)));
        DriversLicense license4 = driversLicenseRepository.saveAndFlush(
                new DriversLicense().setDriversLicenseNumber(531342L)
                        .setCategory(LicenseCategory.C)
                        .setExpirationTime(new Date(2024-9-10)));

        Driver driver = driverRepository.save(new Driver().setLicense(license));
        Driver driver1 = driverRepository.save(new Driver().setLicense(license2));
        Driver driver2 = driverRepository.save(new Driver().setLicense(license3));
        Driver driver3 = driverRepository.save(new Driver().setLicense(license4));

        Car car = carRepository.saveAndFlush(new Car().setCarNumber("A503CH").setType(CarType.PASSENGER_CAR));
        Car car1 = carRepository.saveAndFlush(new Car().setCarNumber("B456MO").setType(CarType.TRUCK));
        Car car2 = carRepository.saveAndFlush(new Car().setCarNumber("C371XB").setType(CarType.BUS));
        Car car3 = carRepository.saveAndFlush(new Car().setCarNumber("H981KE").setType(CarType.TRUCK));

        driver.addCar(car);
        driver.addCar(car1);
        driver.addCar(car2);

        driverRepository.saveAndFlush(driver);*/

    }
}
