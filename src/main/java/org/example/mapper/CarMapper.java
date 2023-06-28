package org.example.mapper;

import org.example.model.Car;
import org.example.model.command.CreateCarCommand;
import org.example.model.command.UpdateCarCommand;
import org.example.model.dto.CarDto;
import org.springframework.stereotype.Service;

@Service
public class CarMapper {

    public Car fromDto(CreateCarCommand command){
        return Car.builder()
                .brand(command.getBrand())
                .model(command.getModel())
                .price(command.getPrice())
                .fuelType(command.getFuelType())
                .deleted(false)
                .build();
    }

    public CarDto toDto(Car car){
        return CarDto.builder()
                .brand(car.getBrand())
                .model(car.getModel())
                .price(car.getPrice())
                .fuelType(car.getFuelType())
                .deleted(false)
                .build();
    }

    public void update(UpdateCarCommand source, Car target){
        target.setBrand(source.getBrand());
        target.setModel(source.getModel());
        target.setPrice(source.getPrice());
        target.setFuelType(source.getFuelType());
        target.setDeleted(false);
    }
}
