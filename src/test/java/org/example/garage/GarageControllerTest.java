package org.example.garage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Garage;
import org.example.model.command.CreateGarageCommand;
import org.example.model.command.UpdateGarageCommand;
import org.example.repository.GarageRepository;
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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GarageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GarageRepository garageRepository;

    @Autowired
    ObjectMapper objectMapper;

    private Garage garage;

    @BeforeEach
    void init(){
        garage = Garage.builder()
                .id(1L)
                .address("address")
                .numberOfPlaces(10)
                .acceptsLPG(true)
                .build();
        garageRepository.save(garage);
    }

    @Test
    void testFindById_ResultInGarageBeingFound() throws Exception {

        Assertions.assertTrue(garageRepository.findById(garage.getId()).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/garages/{id}", garage.getId())
                    .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(garage.getId()))
                .andExpect(jsonPath("$.address").value(garage.getAddress()))
                .andExpect(jsonPath("$.numberOfPlaces").value(garage.getNumberOfPlaces()))
                .andExpect(jsonPath("$.acceptsLPG").value(garage.isAcceptsLPG()));
    }


    @Test
    public void testFindById_ResultInGarageNotFound() throws Exception {
        long nonExistingGarageId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/garages/{id}", nonExistingGarageId)
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void testFindAll_ResultsCarListBeingReturned() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.get("/api/garages")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(garage.getId()))
                .andExpect(jsonPath("$.[0].address").value(garage.getAddress()))
                .andExpect(jsonPath("$.[0].numberOfPlaces").value(garage.getNumberOfPlaces()))
                .andExpect(jsonPath("$.[0].acceptsLPG").value(garage.isAcceptsLPG()))
                ;
    }

    @Test
    void testSave_ResultInGarageBeingSaved() throws Exception {
        CreateGarageCommand command = new CreateGarageCommand();
        command.setAddress("address");
        command.setNumberOfPlaces(10);
        command.setAcceptsLPG(true);

        assertFalse(garageRepository.findById(2L).isPresent());

        mockMvc.perform(post("/api/garages")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.address").value(command.getAddress()))
                .andExpect(jsonPath("$.numberOfPlaces").value(command.getNumberOfPlaces()))
                .andExpect(jsonPath("$.acceptsLPG").value(command.isAcceptsLPG()));

        Assertions.assertTrue(garageRepository.findById(2L).isPresent());
    }

    @Test
    void testDelete_ResultInGarageBeingDelete() throws Exception {
        assertFalse(garageRepository.findById(garage.getId())
                .map(Garage::isDeleted)
                .orElseThrow());

        mockMvc.perform(delete("/api/garages/" + garage.getId())
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        Assertions.assertTrue(garageRepository.findById(garage.getId())
                .map(Garage::isDeleted)
                .orElseThrow());
    }

    @Test
    void testUpdate_ResultInGarageBeingUpdate() throws Exception {
        UpdateGarageCommand updateCommand = new UpdateGarageCommand();
        updateCommand.setAddress("address2");
        updateCommand.setNumberOfPlaces(15);
        updateCommand.setAcceptsLPG(true);

        assertFalse(garageRepository.findById(2L).isPresent());

        mockMvc.perform(post("/api/garages")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("user:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.address").value(updateCommand.getAddress()))
                .andExpect(jsonPath("$.numberOfPlaces").value(updateCommand.getNumberOfPlaces()))
                .andExpect(jsonPath("$.acceptsLPG").value(updateCommand.isAcceptsLPG()));

        assertTrue(garageRepository.findById(2L).isPresent());
    }

}
