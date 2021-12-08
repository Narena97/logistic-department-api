package com.example.testtask.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "driver")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Driver {

    @Id
    @Column(name = "driver_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "drivers_license_id")
    private DriversLicense license;

    @Size(max = 3)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Car> cars = new ArrayList<>();

    public Driver(DriversLicense license) {
        this.license = license;
    }

    public void addCar(Car car) {
        cars.add(car);
        car.setDriver(this);
    }

    public void addCars(List<Car> carsRequest) {
        cars.addAll(carsRequest);
        cars.forEach(car -> car.setDriver(this));
    }

    public void removeCar(Car car) {
        cars.remove(car);
        car.setDriver(null);
    }

}
