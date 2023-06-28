package org.example.car;

import org.example.mapper.CarMapper;
import org.example.model.Car;
import org.example.model.dto.CarDto;
import org.example.model.command.CreateCarCommand;
import org.example.model.command.UpdateCarCommand;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.example.service.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CarServiceImplTest {

    private CarService carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @Captor
    private ArgumentCaptor<Car> carCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carService = new CarServiceImpl(carRepository, carMapper);
    }

    @Test
    void save_shouldSaveCarAndReturnDto() {
        CreateCarCommand command = new CreateCarCommand();
        Car carToSave = new Car();
        Car savedCar = new Car();
        CarDto expectedDto = new CarDto();

        when(carRepository.save(any(Car.class))).thenReturn(savedCar);

        CarDto resultDto = carService.save(command);

        verify(carRepository).save(carCaptor.capture());
        Car capturedCar = carCaptor.getValue();
        assertEquals(command.getBrand(), capturedCar.getBrand());
        assertEquals(command.getModel(), capturedCar.getModel());
    }


    @Test
    void getCarById_shouldReturnDtoIfCarExists() {
        Long carId = 1L;
        Car car = new Car();

        when(carRepository.findByIdAndDeletedFalse(carId)).thenReturn(Optional.of(car));

        Optional<CarDto> resultDto = carService.getCarById(carId);

        assertTrue(resultDto.isPresent());
        verify(carRepository).findByIdAndDeletedFalse(carId);

    }

    @Test
    void getCarById_shouldReturnEmptyOptionalIfCarDoesNotExist() {
        Long carId = 1L;

        when(carRepository.findByIdAndDeletedFalse(carId)).thenReturn(Optional.empty());

        Optional<CarDto> resultDto = carService.getCarById(carId);

        assertFalse(resultDto.isPresent());
        verify(carRepository).findByIdAndDeletedFalse(carId);
    }

    @Test
    void getAllCars_shouldReturnListOfDtos() {
        Car car1 = new Car();
        Car car2 = new Car();
        List<Car> carList = List.of(car1, car2);
        CarDto dto1 = new CarDto();
        CarDto dto2 = new CarDto();
        List<CarDto> expectedDtos = List.of(dto1, dto2);

        when(carRepository.findAll()).thenReturn(carList);

        List<CarDto> resultDtos = carService.getAllCars();

        verify(carRepository).findAll();
    }

    @Test
    void findCarById_shouldReturnCarIfExists() {
        Long carId = 1L;
        Car expectedCar = new Car();

        when(carRepository.findByIdAndDeletedFalse(carId)).thenReturn(Optional.of(expectedCar));

        Optional<Car> resultCar = carService.findCarById(carId);

        assertTrue(resultCar.isPresent());
        assertSame(expectedCar, resultCar.get());
        verify(carRepository).findByIdAndDeletedFalse(carId);
    }

    @Test
    void findCarById_shouldReturnEmptyOptionalIfCarDoesNotExist() {
        Long carId = 1L;

        when(carRepository.findByIdAndDeletedFalse(carId)).thenReturn(Optional.empty());

        Optional<Car> resultCar = carService.findCarById(carId);

        assertFalse(resultCar.isPresent());
        verify(carRepository).findByIdAndDeletedFalse(carId);
    }

    @Test
    void updateCar_shouldUpdateCarAndReturnDto() {
        Long carId = 1L;
        UpdateCarCommand command = new UpdateCarCommand();
        Car carToUpdate = new Car();
        Car updatedCar = new Car();
        carToUpdate.setId(carId);

        when(carRepository.findById(carId)).thenReturn(Optional.of(carToUpdate));
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);

        CarDto resultDto = carService.updateCar(carId, command);

        verify(carRepository).findById(carId);
        verify(carRepository).save(carCaptor.capture());
    }


    @Test
    void updateCar_shouldThrowEntityNotFoundExceptionIfCarDoesNotExist() {
        Long carId = 1L;
        UpdateCarCommand command = new UpdateCarCommand();

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> carService.updateCar(carId, command));
        verify(carRepository).findById(carId);
        verify(carMapper, never()).update(eq(command), any(Car.class));
        verify(carRepository, never()).save(any());
        verify(carMapper, never()).toDto(any());
    }

    @Test
    void deleteCar_shouldMarkCarAsDeleted() {
        // Arrange
        Long carId = 1L;
        Car car = new Car();

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        // Act
        carService.deleteCar(carId);

        // Assert
        assertTrue(car.isDeleted());
        verify(carRepository).findById(carId);
        verify(carRepository).save(car);
    }

    @Test
    void deleteCar_shouldThrowEntityNotFoundExceptionIfCarDoesNotExist() {
        // Arrange
        Long carId = 1L;

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> carService.deleteCar(carId));
        verify(carRepository).findById(carId);
        verify(carRepository, never()).save(any());
    }
}
