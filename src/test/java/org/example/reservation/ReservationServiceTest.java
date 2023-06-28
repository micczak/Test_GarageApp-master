package org.example.reservation;

import org.example.model.Car;
import org.example.model.Garage;
import org.example.model.Reservation;
import org.example.model.command.CreateReservationCommand;
import org.example.model.command.UpdateReservationCommand;
import org.example.model.dto.ReservationDto;
import org.example.repository.ReservationRepository;
import org.example.service.CarService;
import org.example.service.GarageService;
import org.example.service.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private CarService carService;
    @Mock
    private GarageService garageService;

    @InjectMocks
    private ReservationServiceImpl reservationService ;

    private Car car;
    private Garage garage;
    private Reservation reservation;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);

        reservationService = new ReservationServiceImpl(reservationRepository, carService, garageService);

        car = new Car();
        car.setId(1L);
        car.setFuelType("Fuel");
        garage = new Garage();
        garage.setId(1L);
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCar(car);
        reservation.setGarage(garage);
    }

    @Test
    public void testAddReservation() {
        CreateReservationCommand command = new CreateReservationCommand();
        command.setGarageId(1L);
        command.setCarId(1L);
        command.setFromDate(LocalDate.now());
        command.setToDate(LocalDate.now().plusDays(1));

        when(carService.findCarById(command.getCarId())).thenReturn(Optional.of(car));
        when(garageService.findGarageById(command.getGarageId())).thenReturn(Optional.of(garage));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationDto savedReservationDto = reservationService.save(command);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void getAllReservations_ReturnsListOfReservationDto() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        when(carService.findCarById(anyLong())).thenReturn(Optional.of(car));
        when(garageService.findGarageById(anyLong())).thenReturn(Optional.of(garage));
        when(reservationRepository.findByDeletedFalse()).thenReturn(reservations);

        List<ReservationDto> reservationDtos = reservationService.getAllReservations();

        assertNotNull(reservationDtos);
        assertFalse(reservationDtos.isEmpty());

    }

    @Test
    void getReservationById_shouldReturnReservationDtoWhenFound() {
        Long reservationId = 1L;
        ReservationDto expectedDto = new ReservationDto();

        when(reservationRepository.findByIdAndDeletedFalse(reservationId)).thenReturn(Optional.of(reservation));

        Optional<ReservationDto> resultDto = reservationService.getReservationById(reservationId);

        verify(reservationRepository).findByIdAndDeletedFalse(reservationId);


    }

    @Test
    void getReservationById_shouldReturnEmptyOptionalWhenNotFound() {
        Long reservationId = 1L;

        when(reservationRepository.findByIdAndDeletedFalse(reservationId)).thenReturn(Optional.empty());

        Optional<ReservationDto> resultDto = reservationService.getReservationById(reservationId);

        verify(reservationRepository).findByIdAndDeletedFalse(reservationId);
    }

    @Test
    public void testUpdateReservation() {
        Long reservationId = 1L;
        Long garageId = 2L;
        Long carId = 3L;

        UpdateReservationCommand command = new UpdateReservationCommand();
        command.setGarageId(garageId);
        command.setCarId(carId);

        Reservation reservation = new Reservation();
        reservation.setId(reservationId);

        Garage garage = new Garage();
        garage.setId(garageId);
        garage.setAcceptsLPG(true);

        Car car = new Car();
        car.setId(carId);
        car.setFuelType("LPG");

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(garageService.findGarageById(garageId)).thenReturn(Optional.of(garage));
        when(carService.findCarById(carId)).thenReturn(Optional.of(car));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        ReservationDto result = reservationService.updateReservation(reservationId, command);

        assertNotNull(result);
        assertEquals(reservationId, result.getId());


        assertEquals(garageId, result.getGarageId());
        assertEquals(carId, result.getCarId());
    }

    @Test
    void deleteReservation_shouldSetDeletedFlagAndSaveReservation() {
        Long reservationId = 1L;
        Reservation reservation = new Reservation();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        reservationService.deleteReservation(reservationId);

        verify(reservationRepository).findById(reservationId);
        verify(reservationRepository).save(reservation);

    }

    @Test
    void checkIfReservationIsFree_shouldReturnTrueWhenNoOverlappingReservations() {
        CreateReservationCommand command = new CreateReservationCommand();
        Garage garage = new Garage();
        List<Reservation> overlappingReservations = Collections.emptyList();

        command.setGarageId(1L);

        when(garageService.findGarageById(command.getGarageId())).thenReturn(Optional.of(garage));
        when(reservationRepository.findOverlappingReservations(command.getFromDate(), command.getToDate(), command.getGarageId())).thenReturn(overlappingReservations);

        boolean result = reservationService.checkIfReservationIsFree(command);

        verify(garageService).findGarageById(command.getGarageId());
        verify(reservationRepository).findOverlappingReservations(command.getFromDate(), command.getToDate(), command.getGarageId());

        assertTrue(result);
    }
}