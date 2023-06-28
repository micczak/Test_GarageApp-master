package org.example.model.command;

import lombok.*;
import org.example.model.Garage;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class UpdateGarageCommand {
    private Long id;

    @NotBlank(message = "highly needed!")
    private String address;

    @Min(value = 0, message = "this must be over 0")
    private int numberOfPlaces;

    private boolean acceptsLPG;

    public void update(Garage entity){
        entity.setId(id);
        entity.setAddress(address);
        entity.setNumberOfPlaces(numberOfPlaces);
        entity.setAcceptsLPG(acceptsLPG);
    }
}