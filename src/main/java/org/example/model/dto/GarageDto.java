package org.example.model.dto;

import lombok.*;
import org.example.model.Car;
import org.example.model.Garage;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GarageDto {
    private Long id;

    @NotBlank
    private String address;

    @Min(value = 0)
    private int numberOfPlaces;

    private boolean acceptsLPG;

    private boolean deleted;

    public static GarageDto fromEntity(Garage garage){
        return GarageDto.builder()
                .id(garage.getId())
                .address(garage.getAddress())
                .numberOfPlaces(garage.getNumberOfPlaces())
                .acceptsLPG(garage.isAcceptsLPG())
                .deleted(garage.isDeleted())
                .build();
    }
}
