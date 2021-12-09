package com.example.testtask.controller;

import com.example.testtask.dto.DriverDto;
import com.example.testtask.dto.DriversLicenseDto;
import com.example.testtask.entity.Car;
import com.example.testtask.entity.Driver;
import com.example.testtask.entity.DriversLicense;
import com.example.testtask.enums.CarType;
import com.example.testtask.enums.LicenseCategory;
import com.example.testtask.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class DriverControllerTest extends ControllerTest {

    public static final Long validDriversLicenseNumber2 = 9745213056L;
    public static final Long notValidDriversLicenseNumber = 98765432101234569L;

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

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(driverDto),
                Void.class,
                id);

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

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(drivers.get(0).getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(drivers.get(0).getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(drivers.get(0).getLicense().getExpirationTime(), validExpirationTime);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertThrows(EntityNotFoundException.class, () -> driverService.updateDriver(wrongDriverId, driverDto));
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

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(driverDto),
                Void.class,
                id);

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

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(driverDto),
                Void.class,
                id);

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

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(driverDto),
                Void.class,
                id);

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

        ResponseEntity<DriverDto> responseEntity = restTemplate.exchange("/api/drivers/{1}",
                HttpMethod.DELETE,
                null,
                DriverDto.class,
                id);

        List<Driver> drivers = driverRepository.findAll();
        List<DriversLicense> licenses = driversLicenseRepository.findAll();

        Assertions.assertEquals(drivers.size(), 0);
        Assertions.assertEquals(licenses.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

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

        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(licenses.size(), 1);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(drivers.get(0).getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(drivers.get(0).getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(drivers.get(0).getLicense().getExpirationTime(), validExpirationTime);
        Assertions.assertThrows(EntityNotFoundException.class, () -> driverService.deleteDriver(wrongDriverId));
    }

    @Test
    @DisplayName("Getting a driver by id")
    public void getDriverTest() {
        Long id = createTestDriver().getId();

        ResponseEntity<DriverDto> responseEntity = restTemplate.getForEntity("/api/drivers/{1}",
                DriverDto.class,
                id);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.FOUND);
        Assertions.assertEquals(responseEntity.getBody().getLicense().getDriversLicenseNumber(), validDriversLicenseNumber);
        Assertions.assertEquals(responseEntity.getBody().getLicense().getCategory(), LicenseCategory.B);
        Assertions.assertEquals(responseEntity.getBody().getLicense().getExpirationTime(), validExpirationTime);
    }

    @Test
    @DisplayName("Getting a driver with wrong id")
    public void getDriverThatIsNotFoundTest() {
        Long wrongDriverId = createTestDriver().getId() + 1L;

        ResponseEntity<DriverDto> responseEntity = restTemplate.getForEntity("/api/drivers/{1}",
                DriverDto.class,
                wrongDriverId);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertNull(responseEntity.getBody().getLicense());
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

    @Test
    @DisplayName("Adding a car to driver")
    public void addCarToDriverTest() {
        Long driverId = createTestDriver().getId();
        Long carId = createTestCar().getId();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/addCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driverId,
                carId);

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().get(0).getCarNumber(), validCarNumber1);
        Assertions.assertEquals(drivers.get(0).getCars().get(0).getType(), CarType.PASSENGER_CAR);
        Assertions.assertEquals(drivers.get(0).getCars().get(0).getDriver().getId(), driverId);
        Assertions.assertEquals(cars.get(0).getDriver().getId(), driverId);
    }

    @Test
    @DisplayName("Adding a car to driver with wrong id")
    public void addCarToDriverThatIsNotFoundTest() {
        Long wrongDriverId = createTestDriver().getId() + 1L;
        Long carId = createTestCar().getId();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/addCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                wrongDriverId,
                carId);

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 0);
        Assertions.assertNull(cars.get(0).getDriver());
    }

    @Test
    @DisplayName("Adding a car with wrong id to driver")
    public void addCarThatIsNotFoundToDriverTest() {
        Long driverId = createTestDriver().getId();
        Long wrongCarId = createTestCar().getId() + 1L;

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/addCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driverId,
                wrongCarId);

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 0);
        Assertions.assertNull(cars.get(0).getDriver());
    }

    @Test
    @DisplayName("Adding a car that driver already has")
    public void addDuplicateCarToDriverTest() {
        Driver driver = addTestCarToTestDriver();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/addCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                driver.getCars().get(0).getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 1);
    }

    @Test
    @DisplayName("Adding a car that already has driver to another driver")
    public void addCarWithDriverToAnotherDriverTest() {
        Driver driver = addTestCarToTestDriver();

        Driver newDriver = createTestDriver1();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/addCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                newDriver.getId(),
                driver.getCars().get(0).getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(drivers.size(), 2);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 1);
        Assertions.assertEquals(drivers.get(1).getCars().size(), 0);
    }

    @Test
    @DisplayName("Adding a car that does not correspond to the category of driver's license")
    public void addCarToDriverWithWrongCategoryTest() {
        Driver driver = createTestDriver();
        Car car = createTestCar1();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/addCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                car.getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 0);
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

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/addCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                car4.getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 4);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 3);
    }

    @Test
    @DisplayName("Adding a car to driver with expired license")
    public void addCarToDriverWithExpiredLicenseTest() {
        DriversLicense license = new DriversLicense(validDriversLicenseNumber, LicenseCategory.B, notValidExpirationTime);
        Driver driver = new Driver(license);
        driverRepository.save(driver);

        Car car = createTestCar();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/addCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                car.getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 0);
        Assertions.assertNull(cars.get(0).getDriver());
    }

    @Test
    @DisplayName("Remove a car from driver")
    public void removeCarFromDriverTest() {
        Driver driver = addTestCarToTestDriver();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/removeCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                driver.getCars().get(0).getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 0);
        Assertions.assertNull(cars.get(0).getDriver());
    }

    @Test
    @DisplayName("Remove a car that does not exists from driver")
    public void removeCarThatIsNotFoundFromDriverTest() {
        Driver driver = addTestCarToTestDriver();
        Long wrongCarId = driver.getCars().get(0).getId() + 1L;

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/removeCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                driver.getId(),
                wrongCarId);

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 1);
        Assertions.assertEquals(cars.get(0).getDriver().getId(), driver.getId());
    }

    @Test
    @DisplayName("Remove a car from driver that does not exists ")
    public void removeCarFromDriverThatIsNotFoundTest() {
        Driver driver = addTestCarToTestDriver();
        Long wrongDriverId = driver.getId() + 1L;

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/drivers/removeCar/{1}?carId={2}",
                HttpMethod.PUT,
                null,
                Void.class,
                wrongDriverId,
                driver.getCars().get(0).getId());

        List<Driver> drivers = driverRepository.findAll();
        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(drivers.size(), 1);
        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(drivers.get(0).getCars().size(), 1);
        Assertions.assertEquals(cars.get(0).getDriver().getId(), driver.getId());
    }

    private Driver addTestCarToTestDriver() {
        Driver driver = createTestDriver();
        Car car = createTestCar();
        driver.addCar(car);
        return driverRepository.save(driver);
    }

}
