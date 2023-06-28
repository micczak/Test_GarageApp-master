package org.example.service;

import org.example.model.Garage;
import org.example.model.command.CreateGarageCommand;
import org.example.model.command.UpdateGarageCommand;
import org.example.model.dto.GarageDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GarageService {
    GarageDto save(CreateGarageCommand command);

    @Transactional(readOnly = true)
    List<GarageDto> getAllGarages();

    @Transactional(readOnly = true)
    Optional<GarageDto> getGarageById(Long id);

    Optional<Garage> findGarageById(Long id);

    void deleteGarage(Long id);

    GarageDto updateGarage(Long id, UpdateGarageCommand command);
}
