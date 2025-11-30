package com.gr1tEnt.dealership.services;

import com.gr1tEnt.dealership.models.Car;
import com.gr1tEnt.dealership.models.CarDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor // Lombok for constructor injection
public class CarService {
    private final CarsRepository carsRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void updateCar(UUID id, CarDto carDto) {
        Car car = carsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid car id: " + id));

        car.setModel(carDto.getModel())
                .setDescription(carDto.getDescription())
                .setColor(carDto.getColor())
                .setMileage(carDto.getMileage())
                .setPrice(carDto.getPrice())
                .setProductionYear(carDto.getProductionYear());

        if (carDto.getImageFile() != null && !carDto.getImageFile().isEmpty()) {
            if (car.getImageFileName() != null) {
                fileStorageService.delete(car.getImageFileName());
            }

            String newFileName = fileStorageService.save(carDto.getImageFile());

            car.setImageFileName(newFileName);
        }
        carsRepository.save(car);
    }

    @Transactional
    public void addCar(CarDto carDto) {
        String fileName = fileStorageService.save(carDto.getImageFile());

        Car car = Car.builder()
                .model(carDto.getModel())
                .description(carDto.getDescription())
                .color(carDto.getColor())
                .mileage(carDto.getMileage())
                .price(carDto.getPrice())
                .productionYear(carDto.getProductionYear())
                .imageFileName(fileName)
                .build();

        carsRepository.save(car);
    }
}
