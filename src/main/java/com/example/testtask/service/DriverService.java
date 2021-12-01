package com.example.testtask.service;

import com.example.testtask.dto.DriverDto;
import com.example.testtask.entity.Car;
import com.example.testtask.entity.Driver;
import com.example.testtask.mapper.CarMapper;
import com.example.testtask.mapper.DriverMapper;
import com.example.testtask.mapper.DriversLicenseMapper;
import com.example.testtask.repository.DriverRepository;
import com.example.testtask.utils.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriversLicenseService driversLicenseService;
    private final CarService carService;
    private final DriverMapper driverMapper;
    private final DriversLicenseMapper driversLicenseMapper;
    private final CarMapper carMapper;

    @Autowired
    public DriverService(DriverRepository driverRepository,
                         DriversLicenseService driversLicenseService,
                         CarService carService,
                         DriverMapper driverMapper,
                         DriversLicenseMapper driversLicenseMapper,
                         CarMapper carMapper) {
        this.driverRepository = driverRepository;
        this.driversLicenseService = driversLicenseService;
        this.carService = carService;
        this.driverMapper = driverMapper;
        this.driversLicenseMapper = driversLicenseMapper;
        this.carMapper = carMapper;
    }

    public DriverDto getDriver(Long id) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        return optionalDriver.map(driverMapper::driverToDriverDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.GET_ENTITY + Messages.NOT_FOUND,
                        Messages.DRIVER, Messages.DRIVER, id)));
    }

    public List<DriverDto> getDrivers() {
        return driverRepository.findAll().stream()
                .map(driverMapper::driverToDriverDto)
                .collect(Collectors.toList());
    }

    public void addDriver(DriverDto driverDto) {
        if (driverDto.getLicense() != null) {
            if (driversLicenseService.licenseIsValid(driverDto.getLicense(), false)) {
                Driver driver = driverMapper.driverDtoToDriver(driverDto);
                driverRepository.save(driver);
            }
        } else {
            //throw new ex
        }
    }

    public void updateDriver(Long id, DriverDto newDriver) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.UPDATE_ENTITY + Messages.NOT_FOUND,
                        Messages.DRIVER, Messages.DRIVER, id)));
        if (driversLicenseService.licenseIsValid(newDriver.getLicense(), true)) {
            driversLicenseMapper.updateDriversLicenseFromDto(newDriver.getLicense(), driver.getLicense());
            driverRepository.save(driver);
        }
    }

    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.DELETE_ENTITY + Messages.NOT_FOUND,
                        Messages.DRIVER, Messages.DRIVER, id)));
        if (driver != null) {
            driverRepository.deleteById(id);
        }
    }

    public void addCarToDriver(Long driverId, Long carId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(null);
        List<Car> cars = driver.getCars();
        Car car = carMapper.carDtoToCar(carService.getCar(carId));
        if (cars.contains(car)) {
            //throw new ex;
        } else {
            driver.addCar(car);
            driverRepository.save(driver);
        }
    }

    public void removeCarFromDriver(Long driverId, Long carId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(null);
        List<Car> cars = driver.getCars();
        Optional<Car> first = cars.stream().filter(car -> car.getId().equals(carId)).findFirst();

        if (first.isPresent()) {
            driver.removeCar(first.get());
            driverRepository.save(driver);
        } else {
            //throw new ex
        }
    }

}
