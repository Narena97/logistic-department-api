package com.example.testtask.entity;

import com.example.testtask.enums.CarType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "car")
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    @Column(name = "car_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "car_number", unique = true, nullable = false)
    private String carNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CarType type;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    public Car(String carNumber, CarType type) {
        this.carNumber = carNumber;
        this.type = type;
    }
}
