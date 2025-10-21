package school.sorokin.eventmanager.locations.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.sorokin.eventmanager.locations.domain.LocationDomain;
import school.sorokin.eventmanager.locations.dto.ConverterToDto;
import school.sorokin.eventmanager.locations.dto.LocationDto;
import school.sorokin.eventmanager.locations.service.LocationService;

import java.util.List;

@Controller
public class LocationController {

    private final LocationService locationService;

    private final ConverterToDto converter;

    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    public LocationController(LocationService locationService, ConverterToDto converter) {
        this.locationService = locationService;
        this.converter = converter;
    }

    @PostMapping("/locations")
    public ResponseEntity<LocationDto> createLocation(@RequestBody @Valid LocationDto locationDto) {
        log.info("POST /locations - Создание локации: {}", locationDto.getName());
        LocationDomain newLocation = locationService.createLocation(converter.convertToDomain(locationDto));
        log.info("POST /locations - локация {} создана", newLocation.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(converter.convertToDto(newLocation));
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<LocationDto> updateLocation(@PathVariable("id") Integer id, @RequestBody @Valid LocationDto locationDto) {
        log.info("PUT /locations/{} - Обновление локации: {}",id, locationDto.getName());
        LocationDomain updateLocation = locationService.updateLocation(id, converter.convertToDomain(locationDto));
        log.info("PUT /locations/{} - Локация обновлена",id);
        return ResponseEntity.ok(converter.convertToDto(updateLocation));
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<LocationDto> findToLocationId(@PathVariable("id") Integer id) {
        log.info("GET /locations/{} - Получение локации",id);
        LocationDomain locationDomain = locationService.findByIdLocation(id);
        log.info("GET /locations/{} - Локация {} получена",id, locationDomain.getName());
        return ResponseEntity.ok(converter.convertToDto(locationDomain));
    }

    @GetMapping("/locations")
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        log.info("GET /locations - Получение всех локаций");
        return converterListHelper();
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") Integer id) {
        log.info("DELETE /locations - удаление локации по id={}",id);
        locationService.deleteLocation(id);
        log.info("DELETE /locations - локация удалена");
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<List<LocationDto>> converterListHelper() {
        return ResponseEntity.ok(locationService.getAllLocations()
                .stream()
                .map(converter::convertToDto)
                .toList());
    }


}
