package com.example.testtask.service;

import com.example.testtask.dto.CarDto;
import com.example.testtask.entity.Car;
import com.example.testtask.exception.DriversLicenseException;
import com.example.testtask.mapper.CarMapper;
import com.example.testtask.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
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

    public CarDto getCar(Long id) throws DriversLicenseException {
        Optional<Car> optionalCar = carRepository.findById(id);
        return optionalCar.map(carMapper::carToCarDto)
                .orElseThrow(() -> new EntityNotFoundException("Could not get car: car with id " + id + " not found"));
    }

    public List<CarDto> getCars() {
        return carRepository.findAll().stream()
                .map(carMapper::carToCarDto)
                .collect(Collectors.toList());
    }

    public void addCar(CarDto carDto) {
        List<Car> cars = carRepository.findAll();
        Optional<Car> first = cars.stream().filter(car -> car.getCarNumber().equals(carDto.getCarNumber())).findFirst();

        if (first.isPresent()) {
            throw new EntityExistsException("Could not add new car: car with car number " + carDto.getCarNumber() + " already exists");
        } else {
            Car car = carMapper.carDtoToCar(carDto);
            carRepository.save(car);
        }
    }

    public void updateCar(Long id, CarDto newCar) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not update car: car with id " + id + " not found"));
        carMapper.updateCarFromDto(newCar, car);
        carRepository.save(car);
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not delete car: car with id " + id + " not found"));
        carRepository.delete(car);
    }

}
