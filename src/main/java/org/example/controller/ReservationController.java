package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.command.CreateReservationCommand;
import org.example.model.command.UpdateReservationCommand;
import org.example.model.dto.ReservationDto;
import org.example.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDto> addReservation(@RequestBody @Valid CreateReservationCommand command) {
        ReservationDto addedReservation = reservationService.save(command);
        return ResponseEntity.created(URI.create("/api/reservations" + addedReservation.getId())).body(addedReservation);
    }

    @GetMapping
    public List<ReservationDto> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        return ResponseEntity.of(reservationService.getReservationById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Long id, @RequestBody @Valid UpdateReservationCommand command) {
        return ResponseEntity.ok(reservationService.updateReservation(id, command));
    }
}