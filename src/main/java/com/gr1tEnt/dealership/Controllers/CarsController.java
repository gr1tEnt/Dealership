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
        return "cars/index";
    }

    @GetMapping("/add")
    public String showAddPage(Model model) {
        model.addAttribute("carDto", new CarDto());
        return "cars/AddCar";
    }

    @PostMapping("/add")
    public String addCar(@Valid @ModelAttribute CarDto carDto,
                         BindingResult result) {

        if (result.hasErrors()) {
            return "cars/AddCar";
        }

        if (carDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("carDto", "imageFile", "Image is required"));
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
                .color(carDto.getDescription())
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

            CarDto carDto = new CarDto();
            carDto.setModel(car.getModel());
            carDto.setDescription(car.getDescription());
            carDto.setColor(car.getColor());
            carDto.setMileage(car.getMileage());
            carDto.setPrice(car.getPrice());
            carDto.setProductionYear(car.getProductionYear());

            model.addAttribute("carDto", carDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/cars";
        }

        return "cars/EditCar";
    }

    @PostMapping("/edit")
    public String editCar(Model model,
                          @Valid @ModelAttribute CarDto carDto,
                          BindingResult result,
                          @RequestParam UUID id) {

        try {
            Car car = carsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid car id:" + id));
            model.addAttribute("car", car);

            if (result.hasErrors()) {
                return "cars/EditCar";
            }

            // deleting old car's image
            if (!carDto.getImageFile().isEmpty()) {
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + car.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (IOException e) {
                    System.out.printf("IOException: %s\n", e.getMessage());
                }

                // saving new image
                MultipartFile image = carDto.getImageFile();
                Date createdAt = new Date();
                String imageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + imageFileName), StandardCopyOption.REPLACE_EXISTING);
                }
                car.setImageFileName(imageFileName);
            }

            car.setModel(carDto.getModel());
            car.setDescription(carDto.getDescription());
            car.setColor(carDto.getColor());
            car.setMileage(carDto.getMileage());
            car.setPrice(carDto.getPrice());
            car.setProductionYear(carDto.getProductionYear());

            carsRepository.save(car);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

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
