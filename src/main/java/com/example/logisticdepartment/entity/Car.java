package com.example.logisticdepartment.entity;

import com.example.logisticdepartment.enums.CarType;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "car")
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    @Column(name = "car_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
