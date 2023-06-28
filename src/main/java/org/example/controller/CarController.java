package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.CarService;
import org.example.model.command.CreateCarCommand;
import org.example.model.dto.CarDto;
import org.example.model.command.UpdateCarCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarDto> save(@RequestBody @Valid CreateCarCommand command) {
        CarDto createdCar = carService.save(command);
        return ResponseEntity.created(URI.create("/api/cars" + createdCar.getId())).body(createdCar);
    }

    @GetMapping
    public List<CarDto> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        return ResponseEntity.of(carService.getCarById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CarDto> updateCar(@PathVariable Long id, @RequestBody @Valid UpdateCarCommand command) {
        return ResponseEntity.ok(carService.updateCar(id, command));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);

    }
}