package com.example.testtask.controller;

import com.example.testtask.dto.CarDto;
import com.example.testtask.service.CarService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    @ApiOperation(value = "Найти автомобиль по id")
    public CarDto getCar(@PathVariable Long id) {
        return carService.getCar(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    @ApiOperation(value = "Найти список всех автомобилей")
    public List<CarDto> getCars() {
        return carService.getCars();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Добавить автомобиль")
    public void addCar(@Valid @RequestBody CarDto carDto) {
        carService.addCar(carDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Обновить автомобиль")
    public void updateCar(@PathVariable Long id, @Valid @RequestBody CarDto car) {
        carService.updateCar(id, car);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Удалить автомобиль")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }

}
