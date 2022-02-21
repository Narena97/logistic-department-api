package com.example.logisticdepartment.controller;

import com.example.logisticdepartment.dto.DriverDto;
import com.example.logisticdepartment.dto.DriverWithCarsDto;
import com.example.logisticdepartment.dto.DriversLicenseDto;
import com.example.logisticdepartment.entity.Car;
import com.example.logisticdepartment.entity.Driver;
import com.example.logisticdepartment.entity.DriversLicense;
import com.example.logisticdepartment.enums.CarType;
import com.example.logisticdepartment.enums.LicenseCategory;
import com.example.logisticdepartment.utils.Messages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DriverControllerTest extends ControllerTest {

    public static final Long validDriversLicenseNumber2 = 9745213056L;
    public static final Long notValidDriversLicenseNumber = 91111111111L;

    public static final LocalDate validExpirationTime2 = LocalDate.of(2028, 9, 15);
    public static final LocalDate notValidExpirationTime = LocalDate.of(2015, 12, 25);

    @Test
    @DisplayName("Adding a driver")
    public void addDriverTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, LicenseCategory.B, validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, licenses.size());
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertEquals(licenses.get(0).getId(), drivers.get(0).getLicense().getId());
        Assertions.assertEquals(validDriversLicenseNumber, licenses.get(0).getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.B, licenses.get(0).getCategory());
        Assertions.assertEquals(validExpirationTime, licenses.get(0).getExpirationTime());
        Assertions.assertEquals(0, driverViolations.size());
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

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, licenses.size());
        Assertions.assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        Assertions.assertEquals(validDriversLicenseNumber, licenses.get(0).getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.B, licenses.get(0).getCategory());
        Assertions.assertEquals(validExpirationTime, licenses.get(0).getExpirationTime());
        Assertions.assertThrows(EntityExistsException.class, () -> driverService.addDriver(driverDto));
        Assertions.assertEquals(0, driverViolations.size());
    }

    @Test
    @DisplayName("Adding a driver without license")
    public void addDriverWithoutLicense() {
        DriverDto driverDto = new DriverDto();

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(0, drivers.size());
        Assertions.assertEquals(0, licenses.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVER_LICENSE_IS_EMPTY, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Adding a driver with expired license")
    public void addDriverWithExpiredLicenseTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, LicenseCategory.B, notValidExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(0, drivers.size());
        Assertions.assertEquals(0, licenses.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVERS_LICENSE_EXPIRED, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Adding a driver with not valid license number")
    public void addDriverWithNotValidLicenseNumberTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(notValidDriversLicenseNumber, LicenseCategory.B, validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(0, drivers.size());
        Assertions.assertEquals(0, licenses.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVERS_LICENSE_NUMBER_IS_NOT_VALID, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Adding a driver without category of license")
    public void addDriverWithoutCategoryOfLicenseTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, null, validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(0, drivers.size());
        Assertions.assertEquals(0, licenses.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVERS_LICENSE_CATEGORY_IS_EMPTY, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Adding a driver without expiration time of license")
    public void addDriverWithoutExpirationTimeOfLicenseTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber, LicenseCategory.C, null);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(0, drivers.size());
        Assertions.assertEquals(0, licenses.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVERS_LICENSE_EXPIRATION_TIME_IS_EMPTY, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Adding a driver without driver's license number")
    public void addDriverWithoutLicenseNumberTest() {
        DriversLicenseDto licenseDto = new DriversLicenseDto(null, LicenseCategory.C, validExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/drivers", driverDto, Void.class);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(0, drivers.size());
        Assertions.assertEquals(0, licenses.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVERS_LICENSE_NUMBER_IS_EMPTY, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Updating a driver")
    public void updateDriverTest() {
        Long id = createTestDriver().getId();

        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber1, LicenseCategory.C, validExpirationTime1);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(driverDto),
                Void.class,
                id);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(validDriversLicenseNumber1, drivers.get(0).getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.C, drivers.get(0).getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime1, drivers.get(0).getLicense().getExpirationTime());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, licenses.size());
        Assertions.assertEquals(0, driverViolations.size());
    }

    @Test
    @DisplayName("Updating a driver that does not exist")
    public void updateDriverThatIsNotFound() {
        Long wrongDriverId = createTestDriver().getId() + 1L;

        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber1, LicenseCategory.C, validExpirationTime1);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(driverDto),
                Void.class,
                wrongDriverId);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(validDriversLicenseNumber, drivers.get(0).getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.B, drivers.get(0).getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime, drivers.get(0).getLicense().getExpirationTime());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, licenses.size());
        Assertions.assertThrows(EntityNotFoundException.class, () -> driverService.updateDriver(wrongDriverId, driverDto));
        Assertions.assertEquals(0, driverViolations.size());
    }

    @Test
    @DisplayName("Updating a driver without license")
    public void updateDriverWithoutLicenseTest() {
        Long id = createTestDriver().getId();

        DriverDto driverDto = new DriverDto();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(driverDto),
                Void.class,
                id);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(validDriversLicenseNumber, drivers.get(0).getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.B, drivers.get(0).getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime, drivers.get(0).getLicense().getExpirationTime());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, licenses.size());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVER_LICENSE_IS_EMPTY, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Updating a driver with not valid license number")
    public void updateDriverWithNotValidLicenseNumberTest() {
        Long id = createTestDriver().getId();

        DriversLicenseDto licenseDto = new DriversLicenseDto(notValidDriversLicenseNumber, LicenseCategory.C, validExpirationTime1);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(driverDto),
                Void.class,
                id);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(validDriversLicenseNumber, drivers.get(0).getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.B, drivers.get(0).getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime, drivers.get(0).getLicense().getExpirationTime());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, licenses.size());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVERS_LICENSE_NUMBER_IS_NOT_VALID, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Updating a driver with expired license")
    public void updateDriverWithExpiredLicenseTest() {
        Long id = createTestDriver().getId();

        DriversLicenseDto licenseDto = new DriversLicenseDto(validDriversLicenseNumber1, LicenseCategory.C, notValidExpirationTime);
        DriverDto driverDto = new DriverDto(licenseDto);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(driverDto),
                Void.class,
                id);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverDto);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(validDriversLicenseNumber, drivers.get(0).getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.B, drivers.get(0).getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime, drivers.get(0).getLicense().getExpirationTime());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, licenses.size());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVERS_LICENSE_EXPIRED, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deleting a driver")
    public void deleteDriverTest() {
        Long id = createTestDriver().getId();

        ResponseEntity<DriverDto> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.DELETE,
                null,
                DriverDto.class,
                id);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(0, drivers.size());
        Assertions.assertEquals(0, licenses.size());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Deleting a driver with wrong id")
    public void deleteDriverThatIsNotFoundTest() {
        Long wrongDriverId = createTestDriver().getId() + 1L;

        ResponseEntity<DriverDto> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.DELETE,
                null,
                DriverDto.class,
                wrongDriverId);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, licenses.size());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(validDriversLicenseNumber, drivers.get(0).getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.B, drivers.get(0).getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime, drivers.get(0).getLicense().getExpirationTime());
        Assertions.assertThrows(EntityNotFoundException.class, () -> driverService.deleteDriver(wrongDriverId));
    }

    @Test
    @DisplayName("Getting a driver by id")
    public void getDriverTest() {
        Long id = createTestDriver().getId();

        ResponseEntity<DriverWithCarsDto> responseEntity = restTemplate.getForEntity("/api/drivers/{1}",
                DriverWithCarsDto.class,
                id);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotEquals(null, responseEntity.getBody().getDriverDto());
        Assertions.assertEquals(validDriversLicenseNumber, responseEntity.getBody().getDriverDto().getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.B, responseEntity.getBody().getDriverDto().getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime, responseEntity.getBody().getDriverDto().getLicense().getExpirationTime());
    }

    @Test
    @DisplayName("Getting a driver with wrong id")
    public void getDriverThatIsNotFoundTest() {
        Long wrongDriverId = createTestDriver().getId() + 1L;

        ResponseEntity<DriverWithCarsDto> responseEntity = restTemplate.getForEntity("/api/drivers/{1}",
                DriverWithCarsDto.class,
                wrongDriverId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertNull(responseEntity.getBody().getDriverDto());
        Assertions.assertThrows(EntityNotFoundException.class, () -> driverService.getDriver(wrongDriverId));
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

        ResponseEntity<List<DriverWithCarsDto>> responseEntity = restTemplate.exchange("/api/drivers", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        List<DriverWithCarsDto> body = responseEntity.getBody();

        Assertions.assertNotEquals(null, body);
        Assertions.assertEquals(3, body.size());
        Assertions.assertEquals(validDriversLicenseNumber, body.get(0).getDriverDto().getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.B, body.get(0).getDriverDto().getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime, body.get(0).getDriverDto().getLicense().getExpirationTime());
        Assertions.assertEquals(validDriversLicenseNumber1, body.get(1).getDriverDto().getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.C, body.get(1).getDriverDto().getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime1, body.get(1).getDriverDto().getLicense().getExpirationTime());
        Assertions.assertEquals(validDriversLicenseNumber2, body.get(2).getDriverDto().getLicense().getDriversLicenseNumber());
        Assertions.assertEquals(LicenseCategory.D, body.get(2).getDriverDto().getLicense().getCategory());
        Assertions.assertEquals(validExpirationTime2, body.get(2).getDriverDto().getLicense().getExpirationTime());
    }

    @Test
    @DisplayName("Adding a car to driver")
    public void addCarToDriverTest() {
        Long driverId = createTestDriver().getId();
        Long carId = createTestCar().getId();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/addCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driverId,
                carId);

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(1, drivers.get(0).getCars().size());
        Assertions.assertEquals(validCarNumber1, drivers.get(0).getCars().get(0).getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, drivers.get(0).getCars().get(0).getType());
        Assertions.assertEquals(driverId, drivers.get(0).getCars().get(0).getDriver().getId());
        Assertions.assertEquals(driverId, cars.get(0).getDriver().getId());
    }

    @Test
    @DisplayName("Adding a car to driver with wrong id")
    public void addCarToDriverThatIsNotFoundTest() {
        Long wrongDriverId = createTestDriver().getId() + 1L;
        Long carId = createTestCar().getId();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/addCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                wrongDriverId,
                carId);

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(0, drivers.get(0).getCars().size());
        Assertions.assertNull(cars.get(0).getDriver());
    }

    @Test
    @DisplayName("Adding a car with wrong id to driver")
    public void addCarThatIsNotFoundToDriverTest() {
        Long driverId = createTestDriver().getId();
        Long wrongCarId = createTestCar().getId() + 1L;

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/addCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driverId,
                wrongCarId);

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(0, drivers.get(0).getCars().size());
        Assertions.assertNull(cars.get(0).getDriver());
    }

    @Test
    @DisplayName("Adding a car that driver already has")
    public void addDuplicateCarToDriverTest() {
        Driver driver = addTestCarToTestDriver();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/addCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                driver.getCars().get(0).getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(1, drivers.get(0).getCars().size());
    }

    @Test
    @DisplayName("Adding a car that already has driver to another driver")
    public void addCarWithDriverToAnotherDriverTest() {
        Driver driver = addTestCarToTestDriver();
        Driver newDriver = createTestDriver1();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/addCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                newDriver.getId(),
                driver.getCars().get(0).getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(2, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(1, drivers.get(0).getCars().size());
        Assertions.assertEquals(0, drivers.get(1).getCars().size());
    }

    @Test
    @DisplayName("Adding a car that does not correspond to the category of driver's license")
    public void addCarToDriverWithWrongCategoryTest() {
        Driver driver = createTestDriver();
        Car car = createTestCar1();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/addCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                car.getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(0, drivers.get(0).getCars().size());
    }

    @Test
    @DisplayName("Adding a car over driver's limit")
    public void addFourthCarToDriverTest() {
        Driver driver = createTestDriver();
        Car car1 = new Car(validCarNumber1, CarType.PASSENGER_CAR);
        Car car2 = new Car(validCarNumber2, CarType.PASSENGER_CAR);
        Car car3 = new Car(validCarNumber3, CarType.PASSENGER_CAR);
        Car car4 = new Car(validCarNumber4, CarType.PASSENGER_CAR);
        carRepository.saveAll(Arrays.asList(car1, car2, car3, car4));
        driver.addCars(Arrays.asList(car1, car2, car3));
        driverRepository.save(driver);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/addCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                car4.getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(4, cars.size());
        Assertions.assertEquals(3, drivers.get(0).getCars().size());
    }

    @Test
    @DisplayName("Adding a car to driver with expired license")
    public void addCarToDriverWithExpiredLicenseTest() {
        DriversLicense license = new DriversLicense(validDriversLicenseNumber, LicenseCategory.B, notValidExpirationTime);
        Driver driver = new Driver(license);
        driverRepository.save(driver);

        Car car = createTestCar();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/addCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                car.getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<DriverDto>> driverViolations = validator.validate(driverMapper.driverToDriverDto(driver));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(0, drivers.get(0).getCars().size());
        Assertions.assertNull(cars.get(0).getDriver());
        Assertions.assertEquals(1, driverViolations.size());
        Assertions.assertEquals(Messages.DRIVERS_LICENSE_EXPIRED, driverViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Remove a car from driver")
    public void removeCarFromDriverTest() {
        Driver driver = addTestCarToTestDriver();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/removeCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                driver.getCars().get(0).getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(0, drivers.get(0).getCars().size());
        Assertions.assertNull(cars.get(0).getDriver());
    }

    @Test
    @DisplayName("Remove a car that does not exists from driver")
    public void removeCarThatIsNotFoundFromDriverTest() {
        Driver driver = addTestCarToTestDriver();
        Long wrongCarId = driver.getCars().get(0).getId() + 1L;

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/removeCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                wrongCarId);

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(1, drivers.get(0).getCars().size());
        Assertions.assertEquals(driver.getId(), cars.get(0).getDriver().getId());
    }

    @Test
    @DisplayName("Remove a car from driver that does not exists ")
    public void removeCarFromDriverThatIsNotFoundTest() {
        Driver driver = addTestCarToTestDriver();
        Long wrongDriverId = driver.getId() + 1L;

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}/removeCar?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                wrongDriverId,
                driver.getCars().get(0).getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(1, drivers.size());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(1, drivers.get(0).getCars().size());
        Assertions.assertEquals(driver.getId(), cars.get(0).getDriver().getId());
    }

    private Driver addTestCarToTestDriver() {
        Driver driver = createTestDriver();
        Car car = createTestCar();
        driver.addCar(car);
        return driverRepository.save(driver);
    }

}
