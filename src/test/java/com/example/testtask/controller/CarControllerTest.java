package com.example.testtask.controller;

import com.example.testtask.dto.CarDto;
import com.example.testtask.entity.Car;
import com.example.testtask.enums.CarType;
import com.example.testtask.utils.Messages;
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
import java.util.List;
import java.util.Set;

public class CarControllerTest extends ControllerTest {

    public static final String notValidCarNumber = "F5045BC97";

    @Test
    @DisplayName("Adding a car")
    public void addCarTest() {
        CarDto carDto = new CarDto(validCarNumber1, CarType.PASSENGER_CAR);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/cars", carDto, Void.class);

        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<CarDto>> violations = validator.validate(carDto);

        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertEquals(validCarNumber1, cars.get(0).getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, cars.get(0).getType());
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Adding a car with an existing car number")
    public void addCarWithNotUniqueCarNumberTest() {
        createTestCar();

        CarDto carDto = new CarDto(validCarNumber1, CarType.TRUCK);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/cars", carDto, Void.class);

        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<CarDto>> violations = validator.validate(carDto);

        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        Assertions.assertEquals(validCarNumber1, cars.get(0).getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, cars.get(0).getType());
        Assertions.assertThrows(EntityExistsException.class, () -> carService.addCar(carDto));
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Adding a car with an empty car number")
    public void addCarWithEmptyCarNumberTest() {
        CarDto carDto = new CarDto(null, CarType.TRUCK);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/cars", carDto, Void.class);

        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<CarDto>> violation = validator.validate(carDto);

        Assertions.assertEquals(0, cars.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, violation.size());
        Assertions.assertEquals(Messages.CAR_NUMBER_IS_EMPTY, violation.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Adding a car with an empty car type")
    public void addCarWithEmptyCarTypeTest() {
        CarDto carDto = new CarDto(validCarNumber1, null);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/cars", carDto, Void.class);

        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<CarDto>> violation = validator.validate(carDto);

        Assertions.assertEquals(0, cars.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, violation.size());
        Assertions.assertEquals(Messages.CAR_TYPE_IS_EMPTY, violation.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Adding a car with an empty car type")
    public void addCarWithDriverIdTest() {
        CarDto carDto = new CarDto(validCarNumber1, CarType.TRUCK, 1L);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/cars", carDto, Void.class);

        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<CarDto>> violation = validator.validate(carDto);

        Assertions.assertEquals(0, cars.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(1, violation.size());
        Assertions.assertEquals(Messages.CAR_DRIVER_ID_IS_PRESENT, violation.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Adding a car with not valid car number")
    public void addCarWithNotValidCarNumberTest() {
        createTestCar();

        CarDto carDto = new CarDto(notValidCarNumber, CarType.TRUCK);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/cars", carDto, Void.class);

        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<CarDto>> violation = validator.validate(carDto);

        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(validCarNumber1, cars.get(0).getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, cars.get(0).getType());
        Assertions.assertEquals(1, violation.size());
        Assertions.assertEquals(Messages.CAR_NUMBER_IS_NOT_VALID, violation.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deleting a car")
    public void deleteCarTest() {
        Long id = createTestCar().getId();

        ResponseEntity<CarDto> responseEntity = restTemplate.exchange("/api/cars/" + id, HttpMethod.DELETE, null, CarDto.class);

        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(0, cars.size());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Deleting a car with wrong id")
    public void deleteCarThatIsNotFoundTest() {
        Long wrongCarId = createTestCar().getId() + 1L;

        ResponseEntity<CarDto> responseEntity = restTemplate.exchange("/api/cars/{1}",
                HttpMethod.DELETE,
                null,
                CarDto.class,
                wrongCarId);

        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(validCarNumber1, cars.get(0).getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, cars.get(0).getType());
        Assertions.assertThrows(EntityNotFoundException.class, () -> carService.deleteCar(wrongCarId));
    }

    @Test
    @DisplayName("Getting a car by id")
    public void getCarTest() {
        Long id = createTestCar().getId();

        ResponseEntity<CarDto> responseEntity = restTemplate.getForEntity("/api/cars/{1}",
                CarDto.class,
                id);

        Assertions.assertNotEquals(null, responseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(validCarNumber1, responseEntity.getBody().getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, responseEntity.getBody().getType());
    }

    @Test
    @DisplayName("Getting a car with wrong id")
    public void getCarThatIsNotFoundTest() {
        Long wrongCarId = createTestCar().getId() + 1L;

        ResponseEntity<CarDto> responseEntity = restTemplate.getForEntity("/api/cars/{1}",
                CarDto.class,
                wrongCarId);

        Assertions.assertNotEquals(null, responseEntity.getBody());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertNull(responseEntity.getBody().getCarNumber());
        Assertions.assertNull(responseEntity.getBody().getType());
        Assertions.assertThrows(EntityNotFoundException.class, () -> carService.getCar(wrongCarId));
    }

    @Test
    @DisplayName("Getting all cars")
    public void getCarsTest() {
        CarDto carDto = new CarDto(validCarNumber1, CarType.PASSENGER_CAR);
        CarDto carDto1 = new CarDto(validCarNumber2, CarType.TRUCK);
        CarDto carDto2 = new CarDto(validCarNumber3, CarType.BUS);

        List<Car> cars = List.of(carMapper.carDtoToCar(carDto), carMapper.carDtoToCar(carDto1), carMapper.carDtoToCar(carDto2));

        carRepository.saveAll(cars);

        ResponseEntity<List<CarDto>> responseEntity = restTemplate.exchange("/api/cars", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        List<CarDto> body = responseEntity.getBody();

        Assertions.assertNotEquals(null, body);
        Assertions.assertEquals(3, body.size());
        Assertions.assertEquals(validCarNumber1, body.get(0).getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, body.get(0).getType());
        Assertions.assertEquals(validCarNumber2, body.get(1).getCarNumber());
        Assertions.assertEquals(CarType.TRUCK, body.get(1).getType());
        Assertions.assertEquals(validCarNumber3, body.get(2).getCarNumber());
        Assertions.assertEquals(CarType.BUS, body.get(2).getType());

    }

    @Test
    @DisplayName("Updating a car")
    public void updateCarTest() {
        Long id = createTestCar().getId();
        CarDto carDto = new CarDto(validCarNumber2, CarType.TRUCK);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/cars/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(carDto),
                Void.class,
                id);

        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<CarDto>> violation = validator.validate(carDto);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(validCarNumber2, cars.get(0).getCarNumber());
        Assertions.assertEquals(CarType.TRUCK, cars.get(0).getType());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(0, violation.size());
    }

    @Test
    @DisplayName("Updating a car that does not exist")
    public void updateCarThatIsNotFound() {
        Long wrongCarId = createTestCar().getId() + 1L;
        CarDto carDto = new CarDto(validCarNumber2, CarType.TRUCK);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/cars/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(carDto),
                Void.class,
                wrongCarId);

        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(validCarNumber1, cars.get(0).getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, cars.get(0).getType());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertThrows(EntityNotFoundException.class, () -> carService.updateCar(wrongCarId, carDto));
    }

    @Test
    @DisplayName("Updating a car with not valid car number")
    public void updateCarWithNotValidCarNumberTest() {
        Long id = createTestCar().getId();
        CarDto carDto = new CarDto(notValidCarNumber, CarType.TRUCK);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/cars/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(carDto),
                Void.class,
                id);

        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<CarDto>> violation = validator.validate(carDto);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(validCarNumber1, cars.get(0).getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, cars.get(0).getType());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(1, violation.size());
        Assertions.assertEquals(Messages.CAR_NUMBER_IS_NOT_VALID, violation.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Updating a car with driver id")
    public void updateCarWithDriverIdTest() {
        Long id = createTestCar().getId();
        CarDto carDto = new CarDto(validCarNumber2, CarType.TRUCK, 1L);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/cars/{1}",
                HttpMethod.PUT,
                new HttpEntity<>(carDto),
                Void.class,
                id);

        List<Car> cars = carRepository.findAll();

        Set<ConstraintViolation<CarDto>> violation = validator.validate(carDto);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(validCarNumber1, cars.get(0).getCarNumber());
        Assertions.assertEquals(CarType.PASSENGER_CAR, cars.get(0).getType());
        Assertions.assertEquals(1, cars.size());
        Assertions.assertEquals(1, violation.size());
        Assertions.assertEquals(Messages.CAR_DRIVER_ID_IS_PRESENT, violation.iterator().next().getMessage());
    }

    protected Car createTestCar() {
        Car car = new Car(validCarNumber1, CarType.PASSENGER_CAR);
        carRepository.save(car);
        return carRepository.findAll().get(0);
    }

}
