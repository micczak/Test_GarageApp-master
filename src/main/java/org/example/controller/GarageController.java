package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.command.CreateGarageCommand;
import org.example.model.command.UpdateGarageCommand;
import org.example.model.dto.GarageDto;
import org.example.service.GarageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/garages")
public class GarageController {
    private final GarageService garageService;

    @PostMapping
    public ResponseEntity<GarageDto> addGarage(@RequestBody @Valid CreateGarageCommand command) {
        GarageDto addedGarage = garageService.save(command);
        return ResponseEntity.created(URI.create("/api/garages" + addedGarage.getId())).body(addedGarage);
    }

    @GetMapping
    public List<GarageDto> getAllGarages() {
        return garageService.getAllGarages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarageDto> getGarageById(@PathVariable Long id) {
        return ResponseEntity.of(garageService.getGarageById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGarage(@PathVariable Long id) {
        garageService.deleteGarage(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GarageDto> updateGarage(@PathVariable Long id, @RequestBody @Valid UpdateGarageCommand command) {
        return ResponseEntity.ok(garageService.updateGarage(id, command));
    }
}