package com.example.testtask.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
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
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.GET_DRIVER, id)));
    }

    public List<DriverDto> getDrivers() {
        return driverRepository.findAll().stream()
                .map(driverMapper::driverToDriverDto)
                .collect(Collectors.toList());
    }

    public void addDriver(DriverDto driverDto) {
        if (driverDto.getLicense() != null) {
            List<DriversLicenseDto> driversLicensesDto = driversLicenseService.getAllLicenses();
            Optional<DriversLicenseDto> first = driversLicensesDto.stream()
                    .filter(license -> license.getDriversLicenseNumber().equals(driverDto.getLicense().getDriversLicenseNumber())).findFirst();

            if (first.isPresent()) {
                throw new EntityExistsException(String.format(Messages.ADD_DRIVER, driverDto.getLicense().getDriversLicenseNumber()));
            } else if (driversLicenseService.licenseIsValid(driverDto.getLicense(), false)) {
                Driver driver = driverMapper.driverDtoToDriver(driverDto);
                driverRepository.save(driver);
            }
        } else {
            throw new ValidationException(Messages.ADD_DRIVER_LICENSE_IS_EMPTY);
        }
    }

    public void updateDriver(Long id, DriverDto newDriver) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.UPDATE_DRIVER, id)));
        if (newDriver.getLicense() != null) {
            if (driversLicenseService.licenseIsValid(newDriver.getLicense(), true)) {
                driversLicenseMapper.updateDriversLicenseFromDto(newDriver.getLicense(), driver.getLicense());
                driverRepository.save(driver);
            }
        } else {
            throw new ValidationException(Messages.UPDATE_DRIVER_LICENSE_IS_EMPTY);
        }
    }

    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.DELETE_DRIVER, id)));
        driverRepository.delete(driver);
    }

    public void addCarToDriver(Long driverId, Long carId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(() ->
                new EntityNotFoundException(String.format(Messages.ADD_CAR_TO_DRIVER_THAT_NOT_FOUND, driverId)));
        List<Car> cars = driver.getCars();
        Car car = carMapper.carDtoToCar(carService.getCar(carId));
        if (cars.contains(car)) {
            throw new ValidationException(String.format(Messages.ADD_DUPLICATE_CAR_TO_DRIVER, carId));
        } else {
            if (cars.size() < 3) {
                if ((driver.getLicense().getCategory() == LicenseCategory.B && car.getType() == CarType.PASSENGER_CAR) ||
                        (driver.getLicense().getCategory() == LicenseCategory.C && car.getType() == CarType.TRUCK) ||
                        (driver.getLicense().getCategory() == LicenseCategory.D && car.getType() == CarType.BUS)) {
                    if (car.getDriver() != null) {
                        driver.addCar(car);
                        driverRepository.save(driver);
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
        } else {
            throw new EntityNotFoundException(String.format(Messages.REMOVE_CAR_THAT_IS_NOT_FOUND_FROM_DRIVER, carId));
        }
    }

}
