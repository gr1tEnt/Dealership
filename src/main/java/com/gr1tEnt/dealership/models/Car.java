package com.gr1tEnt.dealership.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "cars")
@Getter
@Setter
@Builder
@AllArgsConstructor // constructor for Builder
@NoArgsConstructor // constructor for JPA
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String model;
    private String color;
    private int mileage;
    private String imageFileName;

    @Column(name = "production_year")
    private int productionYear;
    private double price;
}
