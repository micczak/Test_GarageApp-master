package org.example.model.dto;

import lombok.*;
import org.example.model.Reservation;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDto {
    private Long id;

    @NotNull
    private Long carId;

    @NotNull
    private Long garageId;

    @NotNull
    private LocalDate fromDate;

    @NotNull
    private LocalDate toDate;

    private boolean deleted;

    public static ReservationDto fromEntity(Reservation reservation){
        return ReservationDto.builder()
                .id(reservation.getId())
                .carId(reservation.getCar().getId())
                .garageId(reservation.getGarage().getId())
                .fromDate(reservation.getFromDate())
                .toDate(reservation.getToDate())
                .build();
    }
}
