package org.example.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@ToString
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String model;


    @NotBlank
    private String brand;


    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price can not be 0")
    private Double price;

    private String fuelType;

    private boolean deleted;
}
