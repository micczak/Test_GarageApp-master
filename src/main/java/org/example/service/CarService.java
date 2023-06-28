package org.example.service;

import org.example.model.Car;
import org.example.model.command.CreateCarCommand;
import org.example.model.command.UpdateCarCommand;
import org.example.model.dto.CarDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CarService {
    CarDto save(CreateCarCommand command);

    @Transactional(readOnly = true)
    Optional<CarDto> getCarById(Long id);

    @Transactional(readOnly = true)
    List<CarDto> getAllCars();

    Optional<Car> findCarById(Long id);

    CarDto updateCar(Long id, UpdateCarCommand command);

    void deleteCar(Long id);

}
