package com.gr1tEnt.dealership.services;

import com.gr1tEnt.dealership.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarsRepository extends JpaRepository<Car, UUID> {
}
