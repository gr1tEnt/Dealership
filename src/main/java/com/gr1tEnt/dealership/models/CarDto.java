package com.gr1tEnt.dealership.models;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CarDto {
    @NotEmpty(message = "The model is required")
    private String model;

    @Size(min = 10, max = 2000, message = "The description should be between 10 and 2000 characters")
    private String description;

    @NotEmpty(message = "The color is required")
    private String color;

    @Min(0)
    @NotNull(message = "Mileage is required")
    private Integer mileage;

    @NotNull(message = "The production year is required")
    private Integer productionYear;

    @Min(value = 0, message = "Price must be a positive number")
    @NotNull(message = "Price is required")
    private double price;

    private MultipartFile imageFile;


}
