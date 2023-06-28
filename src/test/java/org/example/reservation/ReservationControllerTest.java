package org.example.reservation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Car;
import org.example.model.Garage;
import org.example.model.Reservation;
import org.example.model.command.CreateReservationCommand;
import org.example.model.command.UpdateCarCommand;
import org.example.model.command.UpdateReservationCommand;
import org.example.repository.CarRepository;
import org.example.repository.GarageRepository;
import org.example.repository.ReservationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Base64Utils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private GarageRepository garageRepository;

    @Autowired
    ObjectMapper objectMapper;

    private Reservation reservation;
    private Car car;
    private Garage garage;

    @BeforeEach
    void init(){
        car = Car.builder()
                .id(1L)
                .brand("brand")
                .model("model")
                .price(100.0)
                .fuelType("fuel")
                .build();
        carRepository.save(car);

        garage = Garage.builder()
                .id(1L)
                .address("address")
                .numberOfPlaces(10)
                .acceptsLPG(true)
                .build();
        garageRepository.save(garage);

        reservation = Reservation.builder()
                .id(1L)
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().plusDays(1))
                .car(car)
                .garage(garage)
                .build();
        reservationRepository.save(reservation);
    }

    @Test
    void testFindById_ResultInReservationBeingReturned() throws Exception {
        assertTrue(reservationRepository.findById(reservation.getId()).isPresent());

        mockMvc.perform(get("/api/reservations/{id}", reservation.getId())
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservation.getId()))
                .andExpect(jsonPath("$.fromDate").value(reservation.getFromDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.toDate").value(reservation.getToDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.carId").value(reservation.getCar().getId()))
                .andExpect(jsonPath("$.garageId").value(reservation.getGarage().getId()))
        ;
    }

    @Test
    public void testFindById_shouldReturnCarNotFound() throws Exception {
        long nonExistingReservationId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations/{id}", nonExistingReservationId)
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist())
        ;
    }

    @Test
    void testFindAll_ResultsCarListBeingReturned() throws Exception {
        mockMvc.perform(get("/api/reservations")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(reservation.getId()))
                .andExpect(jsonPath("$.[0].fromDate").value(reservation.getFromDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.[0].toDate").value(reservation.getToDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.[0].carId").value(reservation.getCar().getId()))
                .andExpect(jsonPath("$.[0].garageId").value(reservation.getGarage().getId()))
        ;
    }

    @Test
    void testSave_ResultInReservationBeingSaved() throws Exception {
        CreateReservationCommand command = new CreateReservationCommand();
        command.setFromDate(LocalDate.now().plusDays(2));
        command.setToDate(LocalDate.now().plusDays(3));
        command.setCarId(car.getId());
        command.setGarageId(garage.getId());

        Assertions.assertFalse(reservationRepository.findById(2L).isPresent());

        mockMvc.perform(post("/api/reservations/")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.fromDate").value(command.getFromDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.toDate").value(command.getToDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.carId").value(command.getCarId()))
                .andExpect(jsonPath("$.garageId").value(command.getGarageId()));

        assertTrue(reservationRepository.findById(2L).isPresent());

    }

    @Test
    void testDelete_ResultInCarBeingDelete() throws Exception {
        Assertions.assertFalse(reservationRepository.findById(reservation.getId())
                .map(Reservation::isDeleted)
                .orElseThrow());

        mockMvc.perform(delete("/api/reservations/" + reservation.getId())
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        assertTrue(reservationRepository.findById(reservation.getId())
                .map(Reservation::isDeleted)
                .orElseThrow());
    }

    @Test
    void testUpdate_ResultInGarageBeingUpdate() throws Exception {
        UpdateReservationCommand updateCommand = new UpdateReservationCommand();
        updateCommand.setFromDate(LocalDate.now().plusDays(4));
        updateCommand.setToDate(LocalDate.now().plusDays(5));
        updateCommand.setCarId(car.getId());
        updateCommand.setGarageId(garage.getId());

        Assertions.assertFalse(reservationRepository.findById(2L).isPresent());

        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.fromDate").value(updateCommand.getFromDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.toDate").value(updateCommand.getToDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.carId").value(updateCommand.getCarId()))
                .andExpect(jsonPath("$.garageId").value(updateCommand.getGarageId()))
        ;

        assertTrue(reservationRepository.findById(2L).isPresent());
    }

}
