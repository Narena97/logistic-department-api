package com.example.logisticdepartment.service;

import com.example.logisticdepartment.dto.CarDto;
import com.example.logisticdepartment.dto.DriverDto;
import com.example.logisticdepartment.dto.DriversLicenseDto;
import com.example.logisticdepartment.entity.Car;
import com.example.logisticdepartment.entity.Driver;
import com.example.logisticdepartment.enums.CarType;
import com.example.logisticdepartment.enums.LicenseCategory;
import com.example.logisticdepartment.exception.ValidationException;
import com.example.logisticdepartment.mapper.CarMapper;
import com.example.logisticdepartment.mapper.DriverMapper;
import com.example.logisticdepartment.mapper.DriversLicenseMapper;
import com.example.logisticdepartment.repository.DriverRepository;
import com.example.logisticdepartment.utils.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriversLicenseService driversLicenseService;
    private final CarService carService;
    private final DriverMapper driverMapper;
    private final DriversLicenseMapper driversLicenseMapper;
    private final CarMapper carMapper;

    public DriverDto getDriver(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.GET_DRIVER, id)));
        log.info("Driver with id {} was found", id);
        return driverMapper.driverToDriverDto(driver);
    }

    public List<DriverDto> getDrivers() {
        List<DriverDto> drivers = driverRepository.findAll().stream().map(driverMapper::driverToDriverDto).collect(Collectors.toList());
        log.info("Drivers with ids {} were found", drivers.stream().map(DriverDto::getId).collect(Collectors.toList()));
        return drivers;
    }

    public void addDriver(DriverDto driverDto) {
        List<DriversLicenseDto> driversLicensesDto = driversLicenseService.getAllLicenses();
        Optional<DriversLicenseDto> first = driversLicensesDto.stream()
                .filter(license -> license.getDriversLicenseNumber().equals(driverDto.getLicense().getDriversLicenseNumber())).findFirst();

        if (first.isPresent()) {
            throw new EntityExistsException(String.format(Messages.ADD_DRIVER, driverDto.getLicense().getDriversLicenseNumber()));
        }
        Driver driver = driverMapper.driverDtoToDriver(driverDto);
        driverRepository.save(driver);
        log.info("Driver with id {} was saved", driver.getId());
    }

    public void updateDriver(Long id, DriverDto newDriver) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.UPDATE_DRIVER, id)));
        driversLicenseMapper.updateDriversLicenseFromDto(newDriver.getLicense(), driver.getLicense());
        driverRepository.save(driver);
        log.info("Driver with id {} was updated", driver.getId());
    }

    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.DELETE_DRIVER, id)));
        driverRepository.delete(driver);
        log.info("Driver with id {} was deleted", id);
    }

    /**
     * Метод добавляет водителю автомобиль. Происходит проверка валидности водительских прав, проверяется
     * наличие данного автомобиля у водителя, проверяется количество закреплённых автомобилей за водителем
     * (их количество должно быть не больше 3), проверяется соответствие категории водительских прав данного
     * водителя и типа добавляемого автомобиля, проверяется отсутствие водителя у данного автомобиля.
     * @param driverId - id водителя, которому будет добавлен автомобиль
     * @param carId - id автомобиля, который будет добавлен водителю
     */
    public void addCarToDriver(Long driverId, Long carId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(() ->
                new EntityNotFoundException(String.format(Messages.ADD_CAR_TO_DRIVER_THAT_NOT_FOUND, driverId)));
        if (!driver.getLicense().getExpirationTime().isAfter(LocalDate.now())){
            throw new ValidationException(Messages.ADD_CAR_TO_DRIVER_WITH_EXPIRED_LICENSE);
        }
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
