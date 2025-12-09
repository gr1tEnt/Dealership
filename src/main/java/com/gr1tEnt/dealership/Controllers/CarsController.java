package com.gr1tEnt.dealership.Controllers;

import com.gr1tEnt.dealership.models.Car;
import com.gr1tEnt.dealership.models.CarDto;
import com.gr1tEnt.dealership.services.CarService;
import com.gr1tEnt.dealership.services.CarsRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
@Slf4j
public class CarsController {

    private final CarsRepository carsRepository;
    private final CarService carService;

    @GetMapping({"", "/"})
    public String showCars(Model model) {
        List<Car> cars = carService.getAllCars();
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

        if (carDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("carDto", "imageFile", "The image is required"));
        }

        if (result.hasErrors()) {
            return "AddCar";
        }

        try {
            carService.addCar(carDto);
        } catch (Exception e) {
            System.err.println("Error adding car: " + e.getMessage());
            result.addError(new ObjectError("globalError", "Something went wrong: " + e.getMessage()));
            return "AddCar";
        }

        return "redirect:/cars";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam UUID id) {

        Car car = carService.getCarById(id);
        model.addAttribute("car", car);
        model.addAttribute("carDto", mapToDto(car));

        return "EditCar";
    }

    @PutMapping("/edit")
    public String editCar(Model model,
                          @Valid @ModelAttribute CarDto carDto,
                          BindingResult result,
                          @RequestParam UUID id) {

        if (result.hasErrors()) {
            return "EditCar";
        }

        try {
            carService.updateCar(id, carDto);
        } catch (Exception e) {
            result.addError(new ObjectError("global", "Error updating car: " + e.getMessage()));
            return "EditCar";
        }

        return "redirect:/cars";
    }

    @DeleteMapping("/delete")
    public String deleteCar(@RequestParam UUID id,
                            RedirectAttributes redirectAttributes) {

        try {
            carService.deleteCar(id);

            redirectAttributes.addFlashAttribute("successMessage", "Car deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete car: " + e.getMessage());
        }

        return "redirect:/cars";
    }

    private CarDto mapToDto(Car car) {
        return CarDto.builder()
                .model(car.getModel())
                .description(car.getDescription())
                .color(car.getColor())
                .mileage(car.getMileage())
                .price(car.getPrice())
                .productionYear(car.getProductionYear())
                .build();
    }
}
