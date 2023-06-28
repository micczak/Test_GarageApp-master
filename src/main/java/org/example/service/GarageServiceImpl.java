package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.repository.GarageRepository;
import org.example.service.GarageService;
import org.example.model.Garage;
import org.example.model.command.CreateGarageCommand;
import org.example.model.command.UpdateGarageCommand;
import org.example.model.dto.GarageDto;
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
public class GarageServiceImpl implements GarageService {
    private final GarageRepository garageRepository;

    @Override
    public GarageDto save(CreateGarageCommand command) {
        Garage toSave = command.toEntity();
        Garage saved = garageRepository.save(toSave);
        return GarageDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GarageDto> getAllGarages() {
        List<Garage> garages = garageRepository.findByDeletedFalse();
        return garages.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GarageDto> getGarageById(Long id) {
        return garageRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToDto);
    }

    @Override
    public Optional<Garage> findGarageById(Long id) {
        return garageRepository.findById(id);
    }


    @Override
    public void deleteGarage(Long id) {
        Garage garage = garageRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Garage with id={0} has not found", id)));
        garage.setDeleted(true);
        garageRepository.save(garage);
    }

    private GarageDto mapToDto(Garage garage) {
        return GarageDto.builder()
                .id(garage.getId())
                .address(garage.getAddress())
                .numberOfPlaces(garage.getNumberOfPlaces())
                .acceptsLPG(garage.isAcceptsLPG())
                .deleted(garage.isDeleted())
                .build();
    }

    @Override
    public GarageDto updateGarage(Long id, UpdateGarageCommand command) {
        Garage garage = garageRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Garage with id={0} has not found", id)));
        command.update(garage);
        return GarageDto.fromEntity(garageRepository.save(garage));
    }
}