package com.example.testtask.controller;

import com.example.testtask.dto.DriverDto;
import com.example.testtask.dto.DriversLicenseDto;
import com.example.testtask.entity.Driver;
import com.example.testtask.entity.DriversLicense;
import com.example.testtask.enums.LicenseCategory;
import com.example.testtask.exception.ValidationException;
import com.example.testtask.mapper.DriverMapper;
import com.example.testtask.repository.DriverRepository;
import com.example.testtask.repository.DriversLicenseRepository;
import com.example.testtask.service.DriverService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DriverControllerTest {

    public static final Long validDriversLicenseNumber = 1234567890L;
    public static final Long validDriversLicenseNumber1 = 1782394565L;
    public static final Long validDriversLicenseNumber2 = 9745213056L;
    public static final Long notValidDriversLicenseNumber = 98765432101234569L;

    public static final LocalDate validExpirationTime = LocalDate.of(2025, 12, 25);
    public static final LocalDate validExpirationTime1 = LocalDate.of(2024, 10, 5);
    public static final LocalDate validExpirationTime2 = LocalDate.of(2028, 9, 15);
    public static final LocalDate notValidExpirationTime = LocalDate.of(2015, 12, 25);

    private final TestRestTemplate restTemplate;
    private final DriverRepository driverRepository;
    private final DriversLicenseRepository driversLicenseRepository;
    private final DriverService driverService;
    private final DriverMapper driverMapper;

    @Autowired
    public DriverControllerTest(TestRestTemplate restTemplate,
                                DriverRepository driverRepository,
                                DriversLicenseRepository driversLicenseRepository,
                                DriverService driverService,
                                DriverMapper driverMapper) {
        this.restTemplate = restTemplate;
        this.driverRepository = driverRepository;
        this.driversLicenseRepository = driversLicenseRepository;
        this.driverMapper = driverMapper;
        this.driverService = driverService;
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

        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, LicenseCategory.C, validExpirationTime1);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
        Assertions.assertEquals(licenses.get(0).getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(licenses.get(0).getCategory(), LicenseCategory.B);
        Assertions.assertEquals(licenses.get(0).getExpirationTime(), validExpirationTime);
        Assertions.assertThrows(EntityExistsException.class, () -> driverService.addDriver(driverDto));
    }

    @Test
    @DisplayName("Adding a driver without license")
    public void addDriverWithoutLicense() {
        DriverDto driverDto = new DriverDto();

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 0);
        Assertions.assertEquals(licenses.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertThrows(ValidationException.class, () -> driverService.addDriver(driverDto));
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
        Assertions.assertThrows(ValidationException.class, () -> driverService.addDriver(driverDto));
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
        Assertions.assertThrows(ValidationException.class, () -> driverService.addDriver(driverDto));
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
        Assertions.assertThrows(ValidationException.class, () -> driverService.addDriver(driverDto));
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
        Assertions.assertThrows(ValidationException.class, () -> driverService.addDriver(driverDto));
    }

    @Test
    @DisplayName("Adding a driver without driver's license number")
    public void addDriverWithoutLicenseNumberTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(null, LicenseCategory.C, validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 0);
        Assertions.assertEquals(licenses.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertThrows(ValidationException.class, () -> driverService.addDriver(driverDto));
    }

    @Test
    @DisplayName("Updating a driver")
    public void updateDriverTest() {
        Long id = createTestDriver().getId();

        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber1, LicenseCategory.C, validExpirationTime1);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/" + id, HttpMethod.PUT, new HttpEntity<>(driverDto), Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(drivers.get(0).getLicense().getDriversLicenseNumber(), validDriversLicenseNumber1);
        Assertions.assertEquals(drivers.get(0).getLicense().getCategory(), LicenseCategory.C);
        Assertions.assertEquals(drivers.get(0).getLicense().getExpirationTime(), validExpirationTime1);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
    }

    @Test
    @DisplayName("Updating a driver that does not exist")
    public void updateDriverThatIsNotFound() {
        Long id = createTestDriver().getId();

        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber1, LicenseCategory.C, validExpirationTime1);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/" + id + 1L, HttpMethod.PUT, new HttpEntity<>(driverDto), Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(drivers.get(0).getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(drivers.get(0).getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(drivers.get(0).getLicense().getExpirationTime(), validExpirationTime);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertThrows(EntityNotFoundException.class, () -> driverService.updateDriver(id + 1L, driverDto));
    }

    @Test
    @DisplayName("Updating a driver without license")
    public void updateDriverWithoutLicenseTest() {
        Long id = createTestDriver().getId();

        DriverDto driverDto = new DriverDto();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/" + id, HttpMethod.PUT, new HttpEntity<>(driverDto), Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(drivers.get(0).getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(drivers.get(0).getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(drivers.get(0).getLicense().getExpirationTime(), validExpirationTime);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertThrows(ValidationException.class, () -> driverService.updateDriver(id, driverDto));
    }

    @Test
    @DisplayName("Updating a driver with not valid license number")
    public void updateDriverWithNotValidLicenseNumberTest() {
        Long id = createTestDriver().getId();

        DriversLicenseDto licenseDto = new DriversLicenseDto(notValidDriversLicenseNumber, LicenseCategory.C, validExpirationTime1);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/" + id, HttpMethod.PUT, new HttpEntity<>(driverDto), Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(drivers.get(0).getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(drivers.get(0).getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(drivers.get(0).getLicense().getExpirationTime(), validExpirationTime);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertThrows(ValidationException.class, () -> driverService.updateDriver(id, driverDto));
    }

    @Test
    @DisplayName("Updating a driver with expired license")
    public void updateDriverWithExpiredLicenseTest() {
        Long id = createTestDriver().getId();

        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber1, LicenseCategory.C, notValidExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/" + id, HttpMethod.PUT, new HttpEntity<>(driverDto), Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(drivers.get(0).getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(drivers.get(0).getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(drivers.get(0).getLicense().getExpirationTime(), validExpirationTime);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertThrows(ValidationException.class, () -> driverService.updateDriver(id, driverDto));
    }

    @Test
    @DisplayName("Updating a driver without driver's license number")
    public void updateDriverWithoutLicenseNumberTest() {
        Long id = createTestDriver().getId();

        DriversLicenseDto licenseDto = new DriversLicenseDto(null, LicenseCategory.C, validExpirationTime1);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/" + id, HttpMethod.PUT, new HttpEntity<>(driverDto), Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(drivers.get(0).getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(drivers.get(0).getLicense().getCategory(), LicenseCategory.C);
        Assertions.assertEquals(drivers.get(0).getLicense().getExpirationTime(), validExpirationTime1);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
    }

    @Test
    @DisplayName("Deleting a driver")
    public void deleteDriverTest() {
        Long id = createTestDriver().getId();

        ResponseEntity<DriverDto> responseEntity = restTemplate.exchange("/api/drivers/" + id, HttpMethod.DELETE, null, DriverDto.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 0);
        Assertions.assertEquals(licenses.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }

    @Test
    @DisplayName("Deleting a driver with wrong id")
    public void deleteDriverThatIsNotFoundTest() {
        Long id = createTestDriver().getId();

        ResponseEntity<DriverDto> responseEntity = restTemplate.exchange("/api/drivers/" + id + 1L, HttpMethod.DELETE, null, DriverDto.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(drivers.get(0).getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(drivers.get(0).getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(drivers.get(0).getLicense().getExpirationTime(), validExpirationTime);
        Assertions.assertThrows(EntityNotFoundException.class, () -> driverService.deleteDriver(id + 1L));
    }

    @Test
    @DisplayName("Getting a driver by id")
    public void getDriverTest() {
        Long id = createTestDriver().getId();

        ResponseEntity<DriverDto> responseEntity = restTemplate.getForEntity("/api/drivers/" + id, DriverDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.FOUND);
        Assertions.assertEquals(responseEntity.getBody().getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(responseEntity.getBody().getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(responseEntity.getBody().getLicense().getExpirationTime(), validExpirationTime);
    }

    @Test
    @DisplayName("Getting a driver with wrong id")
    public void getDriverThatIsNotFoundTest() {
        Long id = createTestDriver().getId();

        ResponseEntity<DriverDto> responseEntity = restTemplate.getForEntity("/api/drivers/" + id + 1L, DriverDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertNull(responseEntity.getBody().getLicense());
        Assertions.assertThrows(EntityNotFoundException.class, () -> driverService.getDriver(id + 1L));
    }

    @Test
    @DisplayName("Getting all drivers")
    public void getDriversTest() {
        DriversLicenseDto driversLicenseDto = new DriversLicenseDto(validDriversLicenseNumber, LicenseCategory.B, validExpirationTime);
        DriverDto driverDto = new DriverDto(driversLicenseDto);

        DriversLicenseDto driversLicenseDto1 = new DriversLicenseDto(validDriversLicenseNumber1, LicenseCategory.C, validExpirationTime1);
        DriverDto driverDto1 = new DriverDto(driversLicenseDto1);

        DriversLicenseDto driversLicenseDto2 = new DriversLicenseDto(validDriversLicenseNumber2, LicenseCategory.D, validExpirationTime2);
        DriverDto driverDto2 = new DriverDto(driversLicenseDto2);

        List<Driver> cars = List.of(driverMapper.driverDtoToDriver(driverDto),
                driverMapper.driverDtoToDriver(driverDto1),
                driverMapper.driverDtoToDriver(driverDto2));

        driverRepository.saveAll(cars);

        ResponseEntity<DriverDto[]> responseEntity = restTemplate.getForEntity("/api/drivers", DriverDto[].class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.FOUND);

        DriverDto[] body = responseEntity.getBody();
        Assertions.assertEquals(body.length, 3);
        Assertions.assertEquals(body[0].getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(body[0].getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(body[0].getLicense().getExpirationTime(), validExpirationTime);
        Assertions.assertEquals(body[1].getLicense().getDriversLicenseNumber(), validDriversLicenseNumber1);
        Assertions.assertEquals(body[1].getLicense().getCategory(), LicenseCategory.C);
        Assertions.assertEquals(body[1].getLicense().getExpirationTime(), validExpirationTime1);
        Assertions.assertEquals(body[2].getLicense().getDriversLicenseNumber(), validDriversLicenseNumber2);
        Assertions.assertEquals(body[2].getLicense().getCategory(), LicenseCategory.D);
        Assertions.assertEquals(body[2].getLicense().getExpirationTime(), validExpirationTime2);
    }

    private Driver createTestDriver() {
        DriversLicense license = new DriversLicense(validDriversLicenseNumber, LicenseCategory.B, validExpirationTime);
        Driver driver = new Driver(license);
        driverRepository.save(driver);
        return driverRepository.findAll().get(0);
    }

}
