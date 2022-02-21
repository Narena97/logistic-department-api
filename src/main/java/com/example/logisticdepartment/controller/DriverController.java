package com.example.logisticdepartment.controller;

import com.example.logisticdepartment.dto.DriverDto;
import com.example.logisticdepartment.dto.DriverWithCarsDto;
import com.example.logisticdepartment.service.DriverService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Найти водителя по id")
    public DriverWithCarsDto getDriver(@PathVariable Long id) {
        return driverService.getDriver(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Найти список всех водителей")
    public List<DriverWithCarsDto> getDrivers() {
        return driverService.getDrivers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Добавить водителя")
    public void addDriver(@Valid @RequestBody DriverDto driver) {
        driverService.addDriver(driver);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Обновить водителя")
    public void updateDriver(@PathVariable Long id, @Valid @RequestBody DriverDto driver) {
        driverService.updateDriver(id, driver);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Удалить водителя")
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }

    @PutMapping("/{driverId}/addCar")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Закрепить автомобиль за водителем")
    public void addCarToDriver(@PathVariable Long driverId, @RequestParam Long carId) {
        driverService.addCarToDriver(driverId, carId);
    }

    @PutMapping("/{driverId}/removeCar")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Открепить автомобиль от водителя")
    public void removeCarFromDriver(@PathVariable Long driverId, @RequestParam Long carId) {
        driverService.removeCarFromDriver(driverId, carId);
    }

}
