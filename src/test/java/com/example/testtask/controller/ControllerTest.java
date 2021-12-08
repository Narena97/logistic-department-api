package com.example.testtask.controller;

import com.example.testtask.entity.Car;
import com.example.testtask.entity.Driver;
import com.example.testtask.entity.DriversLicense;
import com.example.testtask.enums.CarType;
import com.example.testtask.enums.LicenseCategory;
import com.example.testtask.mapper.CarMapper;
import com.example.testtask.mapper.DriverMapper;
import com.example.testtask.repository.CarRepository;
import com.example.testtask.repository.DriverRepository;
import com.example.testtask.repository.DriversLicenseRepository;
import com.example.testtask.service.CarService;
import com.example.testtask.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {

    protected static final Long validDriversLicenseNumber = 1234567890L;
    protected static final Long validDriversLicenseNumber1 = 1782394565L;

    protected static final LocalDate validExpirationTime = LocalDate.of(2025, 12, 25);
    protected static final LocalDate validExpirationTime1 = LocalDate.of(2024, 10, 5);

    protected static final String validCarNumber1 = "A504BC 97";
    protected static final String validCarNumber2 = "B279EP 44";
    protected static final String validCarNumber3 = "T671MO 99";
    protected static final String validCarNumber4 = "O524EM 54";

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected CarService carService;

    @Autowired
    protected DriverService driverService;

    @Autowired
    protected CarRepository carRepository;

    @Autowired
    protected DriverRepository driverRepository;

    @Autowired
    protected DriversLicenseRepository driversLicenseRepository;

    @Autowired
    protected CarMapper carMapper;

    @Autowired
    protected DriverMapper driverMapper;

    @BeforeEach
    public void clean() {
        driverRepository.deleteAll();
        carRepository.deleteAll();
        driversLicenseRepository.deleteAll();
    }

    protected Driver createTestDriver() {
        DriversLicense license = new DriversLicense(validDriversLicenseNumber, LicenseCategory.B, validExpirationTime);
        Driver driver = new Driver(license);
        driverRepository.save(driver);
        return driverRepository.findAll().get(0);
    }

    protected Driver createTestDriver1() {
        DriversLicense license = new DriversLicense(validDriversLicenseNumber1, LicenseCategory.B, validExpirationTime1);
        Driver driver = new Driver(license);
        driverRepository.save(driver);
        return driverRepository.findAll().get(1);
    }

    protected Car createTestCar() {
        Car car = new Car(validCarNumber1, CarType.PASSENGER_CAR);
        carRepository.save(car);
        return carRepository.findAll().get(0);
    }

    protected Car createTestCar1() {
        Car car = new Car(validCarNumber1, CarType.TRUCK);
        carRepository.save(car);
        return carRepository.findAll().get(0);
    }

}
