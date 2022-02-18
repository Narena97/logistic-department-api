package com.example.logisticdepartment.controller;

import com.example.logisticdepartment.dto.CarDto;
import com.example.logisticdepartment.service.CarService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Найти автомобиль по id")
    public CarDto getCar(@PathVariable Long id) {
        return carService.getCar(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
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
