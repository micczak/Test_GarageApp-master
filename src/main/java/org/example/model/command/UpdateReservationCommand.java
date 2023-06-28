package org.example.model.command;

import lombok.Data;
import org.example.model.Car;
import org.example.model.Garage;
import org.example.model.Reservation;
import org.example.repository.CarRepository;
import org.example.repository.GarageRepository;
import org.example.service.CarService;
import org.example.service.GarageService;


import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UpdateReservationCommand {

    @NotNull(message = "highly needed!")
    private LocalDate fromDate;

    @NotNull(message = "highly needed!")
    private LocalDate toDate;

    @NotNull(message = "highly needed!")
    private Long carId;

    @NotNull(message = "highly needed!")
    private Long garageId;

    public void update(Reservation reservation, Car car, Garage garage){
        reservation.setFromDate(fromDate);
        reservation.setToDate(toDate);
        reservation.setCar(car);
        reservation.setGarage(garage);
    }

}
