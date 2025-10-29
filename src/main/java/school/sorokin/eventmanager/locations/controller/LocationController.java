package school.sorokin.eventmanager.locations.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sorokin.eventmanager.locations.domain.Location;
import school.sorokin.eventmanager.locations.dto.CreatLocationDto;
import school.sorokin.eventmanager.locations.service.LocationService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    public LocationController(LocationService locationService ) {
        this.locationService = locationService;
    }

    @PostMapping()
    public ResponseEntity<CreatLocationDto> createLocation(@RequestBody @Valid CreatLocationDto creatLocationDto) {
        log.info("POST /locations - Создание локации: '{}'", creatLocationDto.getName());
        Location newLocation = locationService.createLocation(creatLocationDto);
        log.info("POST /locations - локация '{}' создана", newLocation.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newLocation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreatLocationDto> updateLocation(@PathVariable("id") Integer id, @RequestBody @Valid CreatLocationDto creatLocationDto) {
        log.info("PUT /locations/{} - Обновление локации:'{}'", id, creatLocationDto.getName());
        Location updateLocation = locationService.updateLocation(id, converter.convertToDomain(creatLocationDto));
        log.info("PUT /locations/{} - Локация обновлена", id);
        return ResponseEntity.ok(converter.convertToDto(updateLocation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatLocationDto> findLocationById(@PathVariable("id") Integer id) {
        log.info("GET /locations/{} - Получение локации", id);
        Location locationDomain = locationService.findByIdLocation(id);
        log.info("GET /locations/{} - Локация '{}' получена", id, locationDomain.getName());
        return ResponseEntity.ok(converter.convertToDto(locationDomain));
    }

    @GetMapping()
    public ResponseEntity<List<CreatLocationDto>> getAllLocations() {
        log.info("GET /locations - Получение всех локаций");
        return converterListHelper();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") Integer id) {
        log.info("DELETE /locations - удаление локации по id={}", id);
        locationService.deleteLocation(id);
        log.info("DELETE /locations - локация по id={} удалена", id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<List<CreatLocationDto>> converterListHelper() {
        return ResponseEntity.ok(locationService.getAllLocations()
                .stream()
                .map(converter::convertToDto)
                .toList());
    }


}
