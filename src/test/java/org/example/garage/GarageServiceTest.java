package org.example.garage;

import org.example.model.Garage;
import org.example.model.command.CreateGarageCommand;
import org.example.model.command.UpdateGarageCommand;
import org.example.model.dto.GarageDto;
import org.example.repository.GarageRepository;
import org.example.service.GarageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GarageServiceImplTest {

    private GarageServiceImpl garageService;

    @Mock
    private GarageRepository garageRepository;



    Garage garage;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        garageService = new GarageServiceImpl(garageRepository);

         garage = Garage.builder()
                .id(1L)
                .address("Address")
                .numberOfPlaces(10)
                .acceptsLPG(true)
                .deleted(false)
                .build();
    }

    @Test
    void addGarage_shouldReturnGarageDto() {

        CreateGarageCommand command = new CreateGarageCommand();
        when(garageRepository.save(any(Garage.class))).thenReturn(garage);


        GarageDto result = garageService.save(command);


        assertNotNull(result);
        assertEquals(garage.getId(), result.getId());
        verify(garageRepository, times(1)).save(any(Garage.class));

    }

    @Test
    void getAllGarages_shouldReturnListOfGarageDto() {
        List<Garage> garages = new ArrayList<>();
        garages.add(new Garage());
        garages.add(new Garage());
        when(garageRepository.findByDeletedFalse()).thenReturn(garages);


        List<GarageDto> result = garageService.getAllGarages();


        assertNotNull(result);
        assertEquals(garages.size(), result.size());
    }

    @Test
    void getGarageById_withValidId_shouldReturnGarageDto() {

        Long garageId = 1L;
        Garage garage = new Garage();
        garage.setId(garageId);
        when(garageRepository.findByIdAndDeletedFalse(garageId)).thenReturn(Optional.of(garage));


        Optional<GarageDto> result = garageService.getGarageById(garageId);


        assertTrue(result.isPresent());
        assertEquals(garageId, result.get().getId());
    }

    @Test
    void getGarageById_withInvalidId_shouldReturnEmptyOptional() {

        Long garageId = 1L;
        when(garageRepository.findByIdAndDeletedFalse(garageId)).thenReturn(Optional.empty());


        Optional<GarageDto> result = garageService.getGarageById(garageId);


        assertFalse(result.isPresent());
    }

    @Test
    void deleteGarage_withExistingId_shouldSetDeletedFlagAndSaveGarage() {

        Long garageId = 1L;
        Garage garage = new Garage();
        when(garageRepository.findByIdAndDeletedFalse(garageId)).thenReturn(Optional.of(garage));


        garageService.deleteGarage(garageId);


        assertTrue(garage.isDeleted());
        verify(garageRepository, times(1)).save(garage);
    }

    @Test
    void deleteGarage_withNonExistingId_shouldThrowEntityNotFoundException() {

        Long garageId = 1L;
        when(garageRepository.findByIdAndDeletedFalse(garageId)).thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> garageService.deleteGarage(garageId));
    }

    @Test
    void updateGarage_withExistingId_shouldReturnUpdatedGarageDto() {

        Long garageId = 1L;
        UpdateGarageCommand command = new UpdateGarageCommand();
        Garage existingGarage = new Garage();
        Garage updatedGarage = new Garage();
        updatedGarage.setId(garageId);
        when(garageRepository.findByIdAndDeletedFalse(garageId)).thenReturn(Optional.of(existingGarage));
        when(garageRepository.save(any(Garage.class))).thenReturn(updatedGarage);


        GarageDto result = garageService.updateGarage(garageId, command);


        assertNotNull(result);
        assertEquals(garageId, result.getId());
    }

    @Test
    void updateGarage_withNonExistingId_shouldThrowEntityNotFoundException() {

        Long garageId = 1L;
        UpdateGarageCommand command = new UpdateGarageCommand();
        when(garageRepository.findByIdAndDeletedFalse(garageId)).thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> garageService.updateGarage(garageId, command));
    }


}
