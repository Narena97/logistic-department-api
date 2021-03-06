package com.example.logisticdepartment.controller;

import com.example.logisticdepartment.entity.Car;
import com.example.logisticdepartment.entity.Driver;
import com.example.logisticdepartment.entity.DriversLicense;
import com.example.logisticdepartment.enums.CarType;
import com.example.logisticdepartment.enums.LicenseCategory;
import com.example.logisticdepartment.mapper.CarMapper;
import com.example.logisticdepartment.mapper.DriverMapper;
import com.example.logisticdepartment.repository.CarRepository;
import com.example.logisticdepartment.repository.DriverRepository;
import com.example.logisticdepartment.repository.DriversLicenseRepository;
import com.example.logisticdepartment.service.CarService;
import com.example.logisticdepartment.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import javax.validation.Validation;
import javax.validation.Validator;
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

    protected final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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
