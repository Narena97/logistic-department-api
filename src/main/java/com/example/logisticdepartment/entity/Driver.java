package com.example.logisticdepartment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "driver")
@AllArgsConstructor
@NoArgsConstructor
public class Driver {

    @Id
    @Column(name = "driver_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "drivers_license_id")
    private DriversLicense license;

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
