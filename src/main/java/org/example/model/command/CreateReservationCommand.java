package org.example.model.command;

import lombok.Data;
import org.example.model.Car;
import org.example.model.Garage;
import org.example.model.Reservation;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CreateReservationCommand {
    @NotNull(message = "highly needed!")
    private LocalDate fromDate;

    @NotNull(message = "highly needed!")
    private LocalDate toDate;

    @NotNull(message = "highly needed!")
    private Long carId;

    @NotNull(message = "highly needed!")
    private Long garageId;

    public Reservation toEntity(Car car, Garage garage){
        return Reservation.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .car(car)
                .garage(garage)
                .build();
    }

}
