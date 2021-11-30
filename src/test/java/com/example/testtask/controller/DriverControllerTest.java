package com.example.testtask.controller;

import com.example.testtask.dto.DriverDto;
import com.example.testtask.dto.DriversLicenseDto;
import com.example.testtask.entity.Driver;
import com.example.testtask.entity.DriversLicense;
import com.example.testtask.enums.LicenseCategory;
import com.example.testtask.exception.DriversLicenseException;
import com.example.testtask.mapper.DriversLicenseMapper;
import com.example.testtask.repository.DriverRepository;
import com.example.testtask.repository.DriversLicenseRepository;
import com.example.testtask.service.DriversLicenseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DriverControllerTest {

    public static final Long validDriversLicenseNumber = 1234567890L;
    public static final Long validDriversLicenseNumber1 = 1782394565L;
    public static final Long notValidDriversLicenseNumber = 98765432101234569L;
    public static final Long driversLicenseNumber3 = 9173824650L;

    public static final LocalDate validExpirationTime = LocalDate.of(2025, 12, 25);
    public static final LocalDate validExpirationTime1 = LocalDate.of(2024, 10, 5);
    public static final LocalDate notValidExpirationTime = LocalDate.of(2015, 12, 25);

    private final TestRestTemplate restTemplate;
    private final DriverRepository driverRepository;
    private final DriversLicenseRepository driversLicenseRepository;
    private final DriversLicenseService driversLicenseService;
    private final DriversLicenseMapper driversLicenseMapper;

    @Autowired
    public DriverControllerTest(TestRestTemplate restTemplate,
                                DriverRepository driverRepository,
                                DriversLicenseRepository driversLicenseRepository,
                                DriversLicenseMapper driversLicenseMapper,
                                DriversLicenseService driversLicenseService) {
        this.restTemplate = restTemplate;
        this.driverRepository = driverRepository;
        this.driversLicenseRepository = driversLicenseRepository;
        this.driversLicenseMapper = driversLicenseMapper;
        this.driversLicenseService = driversLicenseService;
    }

    @BeforeEach
    public void clean() {
        driverRepository.deleteAll();
    }

    @Test
    @DisplayName("Adding a driver")
    public void addDriverTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, LicenseCategory.B, validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(drivers.get(0).getLicense().getId(), licenses.get(0).getId());
        Assertions.assertEquals(licenses.get(0).getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(licenses.get(0).getCategory(), LicenseCategory.B);
        Assertions.assertEquals(licenses.get(0).getExpirationTime(), validExpirationTime);
    }

    @Test
    @DisplayName("Adding a driver with non-unique license")
    public void addDriverWithNonUniqueLicenseTest() {
        createTestDriver();

        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber1, LicenseCategory.C, validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
        Assertions.assertEquals(licenses.get(0).getDriversLicenseNumber(), validDriversLicenseNumber1);
        Assertions.assertEquals(licenses.get(0).getCategory(), LicenseCategory.B);
        Assertions.assertEquals(licenses.get(0).getExpirationTime(), validExpirationTime1);
        Assertions.assertThrows(EntityExistsException.class, () -> driversLicenseService.licenseIsValid(licenseDto));
    }

    @Test
    @DisplayName("Adding a driver with expired license")
    public void addDriverWithExpiredLicenseTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, LicenseCategory.B, notValidExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 0);
        Assertions.assertEquals(licenses.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertThrows(DriversLicenseException.class, () -> driversLicenseService.licenseIsValid(licenseDto));
    }

    @Test
    @DisplayName("Adding a driver with not valid license number")
    public void addDriverWithNotValidLicenseNumberTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(notValidDriversLicenseNumber, LicenseCategory.B, validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 0);
        Assertions.assertEquals(licenses.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertThrows(DriversLicenseException.class, () -> driversLicenseService.licenseIsValid(licenseDto));
    }

    @Test
    @DisplayName("Adding a driver without category of license")
    public void addDriverWithoutCategoryOfLicenseTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, null, validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 0);
        Assertions.assertEquals(licenses.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertThrows(DriversLicenseException.class, () -> driversLicenseService.licenseIsValid(licenseDto));
    }

    @Test
    @DisplayName("Adding a driver without category of license")
    public void addDriverWithoutExpirationTimeOfLicenseTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, LicenseCategory.C, null);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 0);
        Assertions.assertEquals(licenses.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertThrows(DriversLicenseException.class, () -> driversLicenseService.licenseIsValid(licenseDto));
    }

    /*@Test
    @DisplayName("Adding a driver with not valid category of license")
    public void addDriverWithNotValidCategoryOfLicense() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, "F", validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 0);
        Assertions.assertEquals(licenses.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }*/

    private Driver createTestDriver() {
        DriversLicense license = new DriversLicense(validDriversLicenseNumber1, LicenseCategory.B, validExpirationTime1);
        Driver driver = new Driver(license);
        driverRepository.save(driver);
        return driverRepository.findAll().get(0);
    }

}
