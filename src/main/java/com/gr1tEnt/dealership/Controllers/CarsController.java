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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

        if(carDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("carDto", "imageFile", "Image is required"));
        }

        return "redirect:/cars";
    }
}
