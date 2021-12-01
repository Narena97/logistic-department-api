package com.example.testtask.service;

import com.example.testtask.dto.CarDto;
import com.example.testtask.entity.Car;
import com.example.testtask.exception.ValidationException;
import com.example.testtask.mapper.CarMapper;
import com.example.testtask.repository.CarRepository;
import com.example.testtask.utils.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public CarDto getCar(Long id) throws ValidationException {
        Optional<Car> optionalCar = carRepository.findById(id);
        return optionalCar.map(carMapper::carToCarDto).orElseThrow(() ->
                new EntityNotFoundException(String.format(Messages.GET_ENTITY + Messages.NOT_FOUND, Messages.CAR, Messages.CAR, id)));
    }

    public List<CarDto> getCars() {
        return carRepository.findAll().stream()
                .map(carMapper::carToCarDto)
                .collect(Collectors.toList());
    }

    public void addCar(CarDto carDto) {
        if (carIsValid(carDto, false)) {
            List<Car> cars = carRepository.findAll();
            Optional<Car> first = cars.stream().filter(car -> car.getCarNumber().equals(carDto.getCarNumber())).findFirst();

            if (first.isPresent()) {
                throw new EntityExistsException(String.format(Messages.ADD_ENTITY + Messages.WITH_NUMBER + Messages.ALREADY_EXISTS,
                        Messages.CAR, Messages.CAR, Messages.CAR, carDto.getCarNumber()));
            } else {
                Car car = carMapper.carDtoToCar(carDto);
                carRepository.save(car);
            }
        }
    }

    public void updateCar(Long id, CarDto newCar) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.UPDATE_ENTITY + Messages.NOT_FOUND, Messages.CAR, Messages.CAR, id)));
        if (carIsValid(newCar, true)) {
            carMapper.updateCarFromDto(newCar, car);
            carRepository.save(car);
        }
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(Messages.DELETE_ENTITY + Messages.NOT_FOUND, Messages.CAR, Messages.CAR, id)));
        carRepository.delete(car);
    }

    private boolean carIsValid(CarDto carDto, boolean isUpdate) {
        if (carDto.getCarNumber() == null && !isUpdate) {
            throw new ValidationException(String.format(Messages.ADD_ENTITY + Messages.NUMBER + Messages.SHOULD_NOT_BE_EMPTY, Messages.CAR, Messages.CAR));
        }
        Pattern pattern = Pattern.compile("^[ABEKMHOPCTYX]\\d{3}[ABEKMHOPCTYX]{2}\\s\\d{2,3}$");
        Matcher matcher = pattern.matcher(carDto.getCarNumber());
        if (!matcher.matches()) {
            throw new ValidationException(isUpdate ?
                    String.format(Messages.UPDATE_ENTITY + Messages.NUMBER + Messages.IS_NOT_VALID, Messages.CAR, Messages.CAR) :
                    String.format(Messages.ADD_ENTITY + Messages.NUMBER + Messages.IS_NOT_VALID, Messages.CAR, Messages.CAR));
        }
        if (carDto.getDriverId() != null) {
            throw new ValidationException(isUpdate ?
                    String.format(Messages.UPDATE_ENTITY + Messages.DRIVER + Messages.SHOULD_NOT_BE_HERE, Messages.CAR, Messages.CAR, Messages.UPDATED) :
                    String.format(Messages.ADD_ENTITY + Messages.DRIVER + Messages.SHOULD_NOT_BE_HERE, Messages.CAR, Messages.CAR, Messages.ADDED));
        }
        if (carDto.getType() == null && !isUpdate) {
            throw new ValidationException(String.format(Messages.ADD_ENTITY + Messages.TYPE + Messages.SHOULD_NOT_BE_EMPTY, Messages.CAR, Messages.CAR));
        }
        return true;
    }

}
