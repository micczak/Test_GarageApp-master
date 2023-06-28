package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.mapper.CarMapper;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.example.model.Car;
import org.example.model.dto.CarDto;
import org.example.model.command.CreateCarCommand;
import org.example.model.command.UpdateCarCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Transactional
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarCommand command) {
       Car toSave = command.toEntity();
       Car saved = carRepository.save(toSave);
       return CarDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarDto> getCarById(Long id) {
        return carRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToDto);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<Car> findCarById(Long id) {
        return carRepository.findByIdAndDeletedFalse(id);
    }


    @Override
    public CarDto updateCar(Long id, UpdateCarCommand command) {
        Car cartoUpdate = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Car with id={0} has not found", id)));
        carMapper.update(command, cartoUpdate);
        return carMapper.toDto(carRepository.save(cartoUpdate));
    }

    @Override
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Car with id={0} has not found", id)));
        car.setDeleted(true);
        carRepository.save(car);
    }


    private CarDto mapToDto(Car car) {
        return CarDto.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .price(car.getPrice())
                .fuelType(car.getFuelType())
                .deleted(car.isDeleted())
                .build();
    }
}
