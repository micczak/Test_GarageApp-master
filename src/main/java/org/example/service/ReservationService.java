package org.example.service;

import org.example.model.Garage;
import org.example.model.Reservation;
import org.example.model.dto.ReservationDto;
import org.example.model.command.CreateReservationCommand;
import org.example.model.command.UpdateReservationCommand;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    ReservationDto save(CreateReservationCommand command);

    List<ReservationDto> getAllReservations();

    Optional<ReservationDto> getReservationById(Long id);

    ReservationDto updateReservation(Long id, UpdateReservationCommand command);

    Optional<Reservation> findReservationById(Long id);

    void deleteReservation(Long id);

    boolean
    checkIfReservationIsFree(CreateReservationCommand command);
}
