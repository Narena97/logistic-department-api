package com.example.testtask.service;

import com.example.testtask.dto.CarDto;
import com.example.testtask.entity.Car;
import com.example.testtask.exception.ValidationException;
import com.example.testtask.mapper.CarMapper;
import com.example.testtask.repository.CarRepository;
import com.example.testtask.utils.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Autowired
    public CarService(CarRepository carRepository,
                      CarMapper carMapper) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
    }

    public CarDto getCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.GET_CAR, id)));
        log.info("Car with id {} was found: {}", id, car);
        return carMapper.carToCarDto(car);
    }

    public List<CarDto> getCars() {
        List<CarDto> cars = carRepository.findAll().stream().map(carMapper::carToCarDto).collect(Collectors.toList());
        log.info("Cars was found: {}", cars);
        return cars;
    }

    public void addCar(CarDto carDto) {
        if (carIsValid(carDto, false)) {
            log.info("Car {} is valid", carDto);
            List<Car> cars = carRepository.findAll();
            Optional<Car> first = cars.stream().filter(car -> car.getCarNumber().equals(carDto.getCarNumber())).findFirst();

            if (first.isPresent()) {
                throw new EntityExistsException(String.format(Messages.ADD_CAR, carDto.getCarNumber()));
            } else {
                Car car = carMapper.carDtoToCar(carDto);
                carRepository.save(car);
                log.info("Car {} was saved", car);
            }
        }
    }

    public void updateCar(Long id, CarDto newCar) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.UPDATE_CAR, id)));
        if (carIsValid(newCar, true)) {
            carMapper.updateCarFromDto(newCar, car);
            carRepository.save(car);
            log.info("Car {} was updated", car);
        }
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(Messages.DELETE_CAR, id)));
        carRepository.delete(car);
        log.info("Car {} was deleted", car);
    }

    private boolean carIsValid(CarDto carDto, boolean isUpdate) {
        if (carDto.getCarNumber() == null && !isUpdate) {
            throw new ValidationException(Messages.ADD_CAR_NUMBER_IS_EMPTY);
        }
        Pattern pattern = Pattern.compile("^[ABEKMHOPCTYX]\\d{3}[ABEKMHOPCTYX]{2}\\s\\d{2,3}$");
        Matcher matcher = pattern.matcher(carDto.getCarNumber());
        if (!matcher.matches()) {
            throw new ValidationException(isUpdate ? Messages.UPDATE_CAR_NUMBER_IS_NOT_VALID : Messages.ADD_CAR_NUMBER_IS_NOT_VALID);
        }
        if (carDto.getDriverId() != null) {
            throw new ValidationException(isUpdate ? Messages.UPDATE_CAR_DRIVER_ID_IS_PRESENT : Messages.ADD_CAR_DRIVER_ID_IS_PRESENT);
        }
        if (carDto.getType() == null && !isUpdate) {
            throw new ValidationException(Messages.ADD_CAR_TYPE_IS_EMPTY);
        }
        return true;
    }

}
