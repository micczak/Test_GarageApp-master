package org.example.model.command;

import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateCarCommand {
    private Long id;

    @NotBlank(message = "highly needed!")
    private String brand;

    @NotBlank(message = "highly needed!")
    private String model;

    @NotNull(message = "highly needed!")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price can not be 0")
    private Double price;

    @NotBlank(message = "highly needed!")
    private String fuelType;

}
