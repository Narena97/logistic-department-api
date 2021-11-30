package com.example.testtask.controller;

import com.example.testtask.dto.CarDto;
import com.example.testtask.entity.Car;
import com.example.testtask.enums.CarType;
import com.example.testtask.mapper.CarMapper;
import com.example.testtask.repository.CarRepository;
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

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {

    public static final String carNumber = "A504BC 97";
    public static final String carNumber1 = "B279EP 44";
    public static final String carNumber2 = "T671MO 99";

    private final TestRestTemplate restTemplate;
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Autowired
    public CarControllerTest(TestRestTemplate restTemplate,
                             CarRepository carRepository,
                             CarMapper carMapper) {
        this.restTemplate = restTemplate;
        this.carRepository = carRepository;
        this.carMapper = carMapper;
    }

    @BeforeEach
    public void clean() {
        carRepository.deleteAll();
    }

    @Test
    @DisplayName("Adding a car")
    public void addCarTest() {
        CarDto carDto = new CarDto(carNumber, CarType.PASSENGER_CAR);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/cars", carDto, Void.class);

        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(cars.get(0).getCarNumber(), carNumber);
        Assertions.assertEquals(cars.get(0).getType(), CarType.PASSENGER_CAR);
    }

    @Test
    @DisplayName("Adding a car with an existing car number")
    public void addCarWithNotUniqueCarNumberTest() {
        createTestCar();

        CarDto carDto = new CarDto(carNumber, CarType.TRUCK);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/cars", carDto, Void.class);

        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
        Assertions.assertEquals(cars.get(0).getCarNumber(), carNumber);
        Assertions.assertEquals(cars.get(0).getType(), CarType.PASSENGER_CAR);
    }

    @Test
    @DisplayName("Deleting a car")
    public void deleteCarTest() {
        Long id = createTestCar().getId();

        ResponseEntity<CarDto> responseEntity = restTemplate.exchange("/api/cars/" + id, HttpMethod.DELETE, null, CarDto.class);

        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(cars.size(), 0);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @DisplayName("Deleting a car with wrong id")
    public void deleteCarThatIsNotFoundTest() {
        Long id = createTestCar().getId();

        ResponseEntity<CarDto> responseEntity = restTemplate.exchange("/api/cars/" + id + 1L, HttpMethod.DELETE, null, CarDto.class);

        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(cars.size(), 1);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(cars.get(0).getCarNumber(), carNumber);
        Assertions.assertEquals(cars.get(0).getType(), CarType.PASSENGER_CAR);
    }

    @Test
    @DisplayName("Getting a car by id")
    public void getCarTest() {
        Long id = createTestCar().getId();

        ResponseEntity<CarDto> responseEntity = restTemplate.getForEntity("/api/cars/" + id, CarDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.FOUND);
        Assertions.assertEquals(responseEntity.getBody().getCarNumber(), carNumber);
        Assertions.assertEquals(responseEntity.getBody().getType(), CarType.PASSENGER_CAR);
    }

    @Test
    @DisplayName("Getting a car with wrong id")
    public void getCarThatIsNotFoundTest() {
        Long id = createTestCar().getId();

        ResponseEntity<CarDto> responseEntity = restTemplate.getForEntity("/api/cars/" + id + 1L, CarDto.class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertNull(responseEntity.getBody().getCarNumber());
        Assertions.assertNull(responseEntity.getBody().getType());
    }

    @Test
    @DisplayName("Getting all cars")
    public void getCarsTest() {
        CarDto carDto = new CarDto(carNumber, CarType.PASSENGER_CAR);
        CarDto carDto1 = new CarDto(carNumber1, CarType.TRUCK);
        CarDto carDto2 = new CarDto(carNumber2, CarType.BUS);

        List<Car> cars = List.of(carMapper.carDtoToCar(carDto), carMapper.carDtoToCar(carDto1), carMapper.carDtoToCar(carDto2));

        carRepository.saveAll(cars);

        ResponseEntity<CarDto[]> responseEntity = restTemplate.getForEntity("/api/cars", CarDto[].class);

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.FOUND);

        CarDto[] body = responseEntity.getBody();
        Assertions.assertEquals(body.length, 3);
        Assertions.assertEquals(body[0].getCarNumber(), carNumber);
        Assertions.assertEquals(body[0].getType(), CarType.PASSENGER_CAR);
        Assertions.assertEquals(body[1].getCarNumber(), carNumber1);
        Assertions.assertEquals(body[1].getType(), CarType.TRUCK);
        Assertions.assertEquals(body[2].getCarNumber(), carNumber2);
        Assertions.assertEquals(body[2].getType(), CarType.BUS);
    }

    @Test
    @DisplayName("Updating a car")
    public void updateCarTest() {
        Long id = createTestCar().getId();
        CarDto carDto = new CarDto(carNumber1, CarType.TRUCK);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/cars/" + id, HttpMethod.PUT, new HttpEntity<>(carDto), Void.class);

        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(cars.get(0).getCarNumber(), carNumber1);
        Assertions.assertEquals(cars.get(0).getType(), CarType.TRUCK);
        Assertions.assertEquals(cars.size(), 1);
    }

    @Test
    @DisplayName("Updating a car that does not exist")
    public void updateCarThatIsNotFound() {
        Long id = createTestCar().getId();
        CarDto carDto = new CarDto(carNumber1, CarType.TRUCK);

        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/cars/" + id + 1L, HttpMethod.PUT, new HttpEntity<>(carDto), Void.class);

        List<Car> cars = carRepository.findAll();

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        Assertions.assertEquals(cars.get(0).getCarNumber(), carNumber);
        Assertions.assertEquals(cars.get(0).getType(), CarType.PASSENGER_CAR);
        Assertions.assertEquals(cars.size(), 1);
    }

    private Car createTestCar() {
        Car car = new Car(carNumber, CarType.PASSENGER_CAR);
        carRepository.save(car);
        return carRepository.findAll().get(0);
    }

}
