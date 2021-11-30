package com.example.testtask.controller;

import com.example.testtask.dto.DriverDto;
import com.example.testtask.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/{id}")
    public DriverDto getDriver(@PathVariable Long id) {
        return driverService.getDriver(id);
    }

    @GetMapping
    public List<DriverDto> getDrivers() {
        return driverService.getDrivers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addDriver(@RequestBody DriverDto driver) {
        driverService.addDriver(driver);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateDriver(@PathVariable Long id, @RequestBody DriverDto driver) {
        driverService.updateDriver(id, driver);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }

    @PutMapping("/addCar/{driverId}")
    @ResponseStatus(HttpStatus.OK)
    public void addCarToDriver(@PathVariable Long driverId, @RequestParam Long carId) {
        driverService.addCarToDriver(driverId, carId);
    }

    @PutMapping("/removeCar/{driverId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeCarFromDriver(@PathVariable Long driverId, @RequestParam Long carId) {
        driverService.removeCarFromDriver(driverId, carId);
    }

}
