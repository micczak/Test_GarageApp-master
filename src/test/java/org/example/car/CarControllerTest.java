package org.example.car;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Car;
import org.example.model.command.CreateCarCommand;
import org.example.model.command.UpdateCarCommand;
import org.example.repository.CarRepository;
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
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Car car1;

    @BeforeEach
    void init(){
        car1 = Car.builder()
                .id(1L)
                .brand("Brand1")
                .model("Model1")
                .price(100.0)
                .fuelType("Fuel1")
                .build();
        carRepository.save(car1);
    }

    @Test
    void testFindById_ResultInCarBeingReturned() throws Exception {

        assertTrue(carRepository.findById(car1.getId()).isPresent());

        mockMvc.perform(get("/api/cars/{id}", car1.getId())
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(car1.getId()))
                .andExpect(jsonPath("$.brand").value(car1.getBrand()))
                .andExpect(jsonPath("$.model").value(car1.getModel()))
                .andExpect(jsonPath("$.price").value(car1.getPrice()))
                .andExpect(jsonPath("$.fuelType").value(car1.getFuelType()))
        ;
    }

    @Test
    public void testFindById_shouldReturnCarNotFound() throws Exception {
        long nonExistingCarId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/cars/{id}", nonExistingCarId)
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist())
        ;
    }

    @Test
    void testFindAll_ResultsCarListBeingReturned() throws Exception {


        mockMvc.perform(get("/api/cars")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(car1.getId()))
                .andExpect(jsonPath("$.[0].brand").value(car1.getBrand()))
                .andExpect(jsonPath("$.[0].model").value(car1.getModel()))
                .andExpect(jsonPath("$.[0].price").value(car1.getPrice()))
                .andExpect(jsonPath("$.[0].fuelType").value(car1.getFuelType()))
                ;
    }

    @Test
    void testSave_ResultInCarBeingSaved() throws Exception {
        CreateCarCommand command = new CreateCarCommand();
        command.setBrand("Brand2");
        command.setModel("Model2");
        command.setPrice(1000.0);
        command.setFuelType("type");

        assertFalse(carRepository.findById(2L).isPresent());

        mockMvc.perform(post("/api/cars")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.brand").value(command.getBrand()))
                .andExpect(jsonPath("$.model").value(command.getModel()))
                .andExpect(jsonPath("$.price").value(command.getPrice()))
                .andExpect(jsonPath("$.fuelType").value(command.getFuelType()));

        assertTrue(carRepository.findById(2L).isPresent());
    }


    @Test
    void testDelete_ResultInCarBeingDelete() throws Exception {
        assertFalse(carRepository.findById(car1.getId())
                .map(Car::isDeleted)
                .orElseThrow());

        mockMvc.perform(delete("/api/cars/" + car1.getId())
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        assertTrue(carRepository.findById(car1.getId())
                .map(Car::isDeleted)
                .orElseThrow());
    }

    @Test
    void testUpdate_ResultInGarageBeingUpdate() throws Exception {
        UpdateCarCommand updateCommand = new UpdateCarCommand();
        updateCommand.setBrand("Brand2");
        updateCommand.setModel("Model2");
        updateCommand.setPrice(300.0);
        updateCommand.setFuelType("type2");

        assertFalse(carRepository.findById(2L).isPresent());

        mockMvc.perform(post("/api/cars")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.model").value(updateCommand.getModel()))
                .andExpect(jsonPath("$.brand").value(updateCommand.getBrand()))
                .andExpect(jsonPath("$.price").value(updateCommand.getPrice()))
                .andExpect(jsonPath("$.fuelType").value(updateCommand.getFuelType()));

        assertTrue(carRepository.findById(2L).isPresent());
    }

}