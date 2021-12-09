package com.example.testtask.service;

import com.example.testtask.dto.CarDto;
import com.example.testtask.dto.DriverDto;
import com.example.testtask.dto.DriversLicenseDto;
import com.example.testtask.entity.Car;
import com.example.testtask.entity.Driver;
import com.example.testtask.enums.CarType;
import com.example.testtask.enums.LicenseCategory;
import com.example.testtask.exception.ValidationException;
import com.example.testtask.mapper.CarMapper;
import com.example.testtask.mapper.DriverMapper;
import com.example.testtask.mapper.DriversLicenseMapper;
import com.example.testtask.repository.DriverRepository;
import com.example.testtask.utils.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.GET_DRIVER, id)));
        log.info("Driver with id {} was found: {}", id, driver);
        return driverMapper.driverToDriverDto(driver);
    }

    public List<DriverDto> getDrivers() {
        List<DriverDto> drivers = driverRepository.findAll().stream().map(driverMapper::driverToDriverDto).collect(Collectors.toList());
        log.info("Drivers was found: {}", drivers);
        return drivers;
    }

    public void addDriver(DriverDto driverDto) {
        if (driverDto.getLicense() != null) {
            List<DriversLicenseDto> driversLicensesDto = driversLicenseService.getAllLicenses();
            Optional<DriversLicenseDto> first = driversLicensesDto.stream()
                    .filter(license -> license.getDriversLicenseNumber().equals(driverDto.getLicense().getDriversLicenseNumber())).findFirst();

            if (first.isPresent()) {
                throw new EntityExistsException(String.format(Messages.ADD_DRIVER, driverDto.getLicense().getDriversLicenseNumber()));
            } else if (driversLicenseService.licenseIsValid(driverDto.getLicense(), false)) {
                log.info("Driver's license {} is valid", driverDto.getLicense());
                Driver driver = driverMapper.driverDtoToDriver(driverDto);
                driverRepository.save(driver);
                log.info("Driver {} was saved", driver);
            }
        } else {
            throw new ValidationException(Messages.ADD_DRIVER_LICENSE_IS_EMPTY);
        }
    }

    public void updateDriver(Long id, DriverDto newDriver) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.UPDATE_DRIVER, id)));
        if (newDriver.getLicense() != null) {
            if (driversLicenseService.licenseIsValid(newDriver.getLicense(), true)) {
                log.info("New driver's license {} is valid", newDriver.getLicense());
                driversLicenseMapper.updateDriversLicenseFromDto(newDriver.getLicense(), driver.getLicense());
                driverRepository.save(driver);
                log.info("Driver {} was updated", driver);
            }
        } else {
            throw new ValidationException(Messages.UPDATE_DRIVER_LICENSE_IS_EMPTY);
        }
    }

    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.DELETE_DRIVER, id)));
        driverRepository.delete(driver);
        log.info("Driver {} was deleted", driver);
    }

    public void addCarToDriver(Long driverId, Long carId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(() ->
                new EntityNotFoundException(String.format(Messages.ADD_CAR_TO_DRIVER_THAT_NOT_FOUND, driverId)));
        List<Car> cars = driver.getCars();
        CarDto carDto = carService.getCar(carId);
        Optional<Car> first = cars.stream().filter(car -> car.getCarNumber().equals(carDto.getCarNumber())).findFirst();
        if (first.isPresent()) {
            throw new ValidationException(String.format(Messages.ADD_DUPLICATE_CAR_TO_DRIVER, carId));
        } else {
            if (cars.size() < 3) {
                if ((driver.getLicense().getCategory() == LicenseCategory.B && carDto.getType() == CarType.PASSENGER_CAR) ||
                        (driver.getLicense().getCategory() == LicenseCategory.C && carDto.getType() == CarType.TRUCK) ||
                        (driver.getLicense().getCategory() == LicenseCategory.D && carDto.getType() == CarType.BUS)) {
                    if (carDto.getDriverId() == null) {
                        driver.addCar(carMapper.carDtoToCar(carDto));
                        driverRepository.save(driver);
                        log.info("Car with id {} was added to driver with id {}", carId, driverId);
                    } else {
                        throw new ValidationException(Messages.ADD_CAR_THAT_HAS_DRIVER_TO_ANOTHER_DRIVER);
                    }
                } else {
                    throw new ValidationException(Messages.ADD_CAR_TO_DRIVER_MISMATCH);
                }
            } else {
                throw new ValidationException(Messages.ADD_CAR_TO_DRIVER_ABOVE_LIMIT);
            }
        }
    }

    public void removeCarFromDriver(Long driverId, Long carId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(() ->
                new EntityNotFoundException(String.format(Messages.REMOVE_CAR_FROM_DRIVER_THAT_NOT_FOUND, driverId)));
        List<Car> cars = driver.getCars();
        Optional<Car> first = cars.stream().filter(car -> car.getId().equals(carId)).findFirst();

        if (first.isPresent()) {
            driver.removeCar(first.get());
            driverRepository.save(driver);
            log.info("Car with id {} was removed from driver with id {}", carId, driverId);
        } else {
            throw new EntityNotFoundException(String.format(Messages.REMOVE_CAR_THAT_IS_NOT_FOUND_FROM_DRIVER, carId));
        }
    }

}
