package com.gr1tEnt.dealership.Controllers;

import com.gr1tEnt.dealership.models.Car;
import com.gr1tEnt.dealership.models.CarDto;
import com.gr1tEnt.dealership.services.CarsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cars")
public class CarsController {

    @Autowired
    private CarsRepository carsRepository;

    @GetMapping({"", "/"})
    public String showCars(Model model) {
        List<Car> cars = carsRepository.findAll(Sort.by(Sort.Direction.ASC, "mileage"));
        model.addAttribute("cars", cars);
        return "index";
    }

    @GetMapping("/add")
    public String showAddPage(Model model) {
        model.addAttribute("carDto", CarDto.builder().build());
        return "AddCar";
    }

    @PostMapping("/add")
    public String addCar(@Valid @ModelAttribute CarDto carDto,
                         BindingResult result) {

        if (carDto.getImageFile() == null || carDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("carDto", "imageFile", "Image is required"));
        }

        if (result.hasErrors()) {
            return "AddCar";
        }

        MultipartFile image = carDto.getImageFile();
        Date createdAt = new Date();
        String imageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + imageFileName), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            System.out.printf("IOException: %s\n", e.getMessage());
        }

        Car car = Car.builder()
                .model(carDto.getModel())
                .description(carDto.getDescription())
                .color(carDto.getColor())
                .mileage(carDto.getMileage())
                .price(carDto.getPrice())
                .productionYear(carDto.getProductionYear())
                .imageFileName(imageFileName)
                .build();
        carsRepository.save(car);

        return "redirect:/cars";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam UUID id) {

        try {
            Car car = carsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid car id:" + id));
            model.addAttribute("car", car);

            CarDto carDto = CarDto.builder()
                    .model(car.getModel())
                    .description(car.getDescription())
                    .color(car.getColor())
                    .mileage(car.getMileage())
                    .price(car.getPrice())
                    .productionYear(car.getProductionYear())
                    .build();

            model.addAttribute("carDto", carDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/cars";
        }

        return "EditCar";
    }

    @PutMapping("/edit")
    public String editCar(Model model,
                          @Valid @ModelAttribute CarDto carDto,
                          BindingResult result,
                          @RequestParam UUID id) {

        // Global Exception Handler will catch the error
        Car car = carsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid car id:" + id));
        model.addAttribute("car", car);

        if (result.hasErrors()) {
            return "EditCar";
        }

        // Deleting old car's image
        if (carDto.getImageFile() != null && !carDto.getImageFile().isEmpty()) {
            String uploadDir = "public/images/";
            Path oldImagePath = Paths.get(uploadDir + car.getImageFileName());

            // Delete old image if it exists
            try {
                if (!Files.exists(oldImagePath)) {
                    Files.delete(oldImagePath);
                }
            } catch (IOException e) {
                System.err.println("Error deleting old image: " + e.getMessage());
                // Continue even if old image deletion fails
            }

            // Saving new image
            MultipartFile image = carDto.getImageFile();
            Date createdAt = new Date();
            String imageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + imageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                    car.setImageFileName(imageFileName);
                }
            } catch (IOException e) {
                System.err.println("Error saving new image: " + e.getMessage());
                result.addError(new FieldError("carDto", "imageFile", "Error uploading image: " + e.getMessage()));
                return "EditCar";
            }
        }

        car.setModel(carDto.getModel());
        car.setDescription(carDto.getDescription());
        car.setColor(carDto.getColor());
        car.setMileage(carDto.getMileage());
        car.setPrice(carDto.getPrice());
        car.setProductionYear(carDto.getProductionYear());

        carsRepository.save(car);

        return "redirect:/cars";
    }

    @DeleteMapping("/delete")
    public String deleteCar(@RequestParam UUID id) {

        try {
            Car car = carsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid car id:" + id));

            // delete car image before deleting the object
            Path imagePath = Paths.get("public/images/" + car.getImageFileName());

            try {
                Files.delete(imagePath);
            } catch (IOException e) {
                System.out.printf("IOException: %s\n", e.getMessage());
            }

            carsRepository.delete(car);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/cars";
    }
}
