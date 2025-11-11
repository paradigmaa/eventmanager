package school.sorokin.eventmanager.locations.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sorokin.eventmanager.locations.dto.CreatLocationDto;
import school.sorokin.eventmanager.locations.dto.ResponseLocationDto;
import school.sorokin.eventmanager.locations.dto.UpdateLocationDto;
import school.sorokin.eventmanager.locations.service.LocationService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping()
    public ResponseEntity<ResponseLocationDto> createLocation(@RequestBody @Valid CreatLocationDto creatLocationDto) {
        log.info("POST /locations - Создание локации: '{}'", creatLocationDto.getName());
        ResponseLocationDto newLocation = locationService.createLocation(creatLocationDto);
        log.info("POST /locations - локация '{}' создана", newLocation.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newLocation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseLocationDto> updateLocation(@PathVariable("id") Integer id, @RequestBody @Valid UpdateLocationDto updateLocationDto) {
        log.info("PUT /locations/{} - Обновление локации:'{}'", id, updateLocationDto.getName());
        ResponseLocationDto updatedLocation = locationService.updateLocation(id, updateLocationDto);
        log.info("PUT /locations/{} - Локация обновлена", id);
        return ResponseEntity.ok(updatedLocation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseLocationDto> findLocationById(@PathVariable("id") Integer id) {
        log.info("GET /locations/{} - Получение локации", id);
        ResponseLocationDto findLocal = locationService.findByIdLocation(id);
        log.info("GET /locations/{} - Локация '{}' получена", id, findLocal.getName());
        return ResponseEntity.ok(findLocal);
    }

    @GetMapping()
    public ResponseEntity<List<ResponseLocationDto>> getAllLocations() {
        log.info("GET /locations - Получение всех локаций");
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") Integer id) {
        log.info("DELETE /locations - удаление локации по id={}", id);
        locationService.deleteLocation(id);
        log.info("DELETE /locations - локация по id={} удалена", id);
        return ResponseEntity.noContent().build();
    }

}
