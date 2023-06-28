package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Car;
import org.example.exceptions.CarNotFoundException;
import org.example.exceptions.GarageNotFoundException;
import org.example.exceptions.ReservationNotFoundException;
import org.example.model.Garage;
import org.example.repository.ReservationRepository;
import org.example.model.Reservation;
import org.example.model.command.CreateReservationCommand;
import org.example.model.command.UpdateReservationCommand;
import org.example.model.dto.ReservationDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final CarService carService;
    private final GarageService garageService;

    @Override
    @Transactional
    public ReservationDto save(@NotNull CreateReservationCommand command) {
        Garage garage = garageService.findGarageById(command.getGarageId()).orElseThrow(() -> new GarageNotFoundException("Garage not found"));

        Car car = carService.findCarById(command.getCarId()).orElseThrow(() -> new CarNotFoundException("Car not found"));

        if(car.getFuelType().equals("LPG") && !garage.isAcceptsLPG()){
            throw new RuntimeException("LPG is not allowed here!");
        }

        if (!checkIfReservationIsFree(command)) {
            throw new RuntimeException("there is no free places for these dates");
        }
        Reservation toSave = command.toEntity(car, garage);
        Reservation saved = reservationRepository.save(toSave);
        return ReservationDto.fromEntity(saved);
    }

    @Override
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findByDeletedFalse().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<ReservationDto> getReservationById(Long id) {
        return reservationRepository.findByIdAndDeletedFalse(id).map(this::mapToDto);
    }

    @Override
    public Optional<Reservation> findReservationById(Long id) {
        return reservationRepository.findById(id);
    }



    @Override
    @Transactional
    public ReservationDto updateReservation(Long id, @NotNull UpdateReservationCommand command) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        Garage garage = garageService.findGarageById(command.getGarageId()).orElseThrow(() -> new GarageNotFoundException("Garage not found"));

        Car car = carService.findCarById(command.getCarId()).orElseThrow(() -> new CarNotFoundException("Car not found"));

        if(car.getFuelType().equals("LPG") && !garage.isAcceptsLPG()){
            throw new RuntimeException("LPG is not allowed here!");
        }
        command.update(reservation, car, garage);
        return ReservationDto.fromEntity(reservationRepository.save(reservation));
    }

    @Override
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        reservation.setDeleted(true);
        reservationRepository.save(reservation);
    }

    @Override
    public boolean checkIfReservationIsFree(@NotNull CreateReservationCommand command) {
       garageService.findGarageById(command.getGarageId()).orElseThrow(() -> new GarageNotFoundException("Garage not found"));

        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(command.getFromDate(), command.getToDate(), command.getGarageId());

        return overlappingReservations.isEmpty();
    }

    private ReservationDto mapToDto(@NotNull Reservation reservation) {
        return ReservationDto.builder()
                .id(reservation.getId()).
                carId(reservation.getCar().getId())
                .garageId(reservation.getGarage().getId())
                .fromDate(reservation.getFromDate())
                .toDate(reservation.getToDate())
                .deleted(reservation.isDeleted())
                .build();
    }
}