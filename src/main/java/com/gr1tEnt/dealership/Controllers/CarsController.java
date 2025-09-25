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

        Car car = new Car();
        car.setModel(carDto.getModel());
        car.setDescription(carDto.getDescription());
        car.setColor(carDto.getColor());
        car.setMileage(carDto.getMileage());
        car.setPrice(carDto.getPrice());
        car.setProductionYear(carDto.getProductionYear());
        car.setImageFileName(imageFileName);
        carsRepository.save(car);

        return "redirect:/cars";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam UUID id) {

        try {
            Car car = carsRepository.findById(id).get();
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
}
