package org.example.model.dto;

import lombok.*;
import org.example.model.Car;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDto {
    private Long id;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    private boolean deleted;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price can not be 0")
    private Double price;

    @NotBlank
    private String fuelType;

    public static CarDto fromEntity(Car car){
        return CarDto.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .price(car.getPrice())
                .fuelType(car.getFuelType())
                .deleted(car.isDeleted())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarDto carDto = (CarDto) o;
        return Objects.equals(brand, carDto.brand) &&
                Objects.equals(model, carDto.model) &&
                Objects.equals(id, carDto.id) &&
                Objects.equals(deleted, carDto.deleted) &&
                Objects.equals(price, carDto.price) &&
                Objects.equals(fuelType, carDto.fuelType);

    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, model, id, deleted, price, fuelType);
    }


}