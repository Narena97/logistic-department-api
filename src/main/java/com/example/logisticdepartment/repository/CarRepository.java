package com.example.logisticdepartment.repository;

import com.example.logisticdepartment.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

}
