package school.sorokin.eventmanager.locations.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.locations.domain.Location;
import school.sorokin.eventmanager.locations.dto.LocationConverter;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.locations.exception.LocationCapacityException;
import school.sorokin.eventmanager.locations.exception.LocationTakenNameException;
import school.sorokin.eventmanager.locations.exception.NotFoundLocationException;
import school.sorokin.eventmanager.locations.repository.LocationRepository;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationConverter locationConverter;

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    public LocationService(LocationRepository locationRepository, LocationConverter locationConverter) {
        this.locationRepository = locationRepository;
        this.locationConverter = locationConverter;
    }

    @Transactional
    public Location createLocation(Location location) {
        log.info("Запрос на создание локации у сервиса");
        checkLocationName(location.getName(), null);
        var newLocation = locationRepository.save(locationConverter.convertToEntity(location));
        log.info("Запрос на создание локации '{}' с id={} у сервиса выполнен",
                newLocation.getName(),
                newLocation.getId());
        return locationConverter.convertToDomain(newLocation);
    }

    @Transactional
    public Location updateLocation(Integer id, Location update) {
        log.info("Запрос на обновление локации без изменения имени");
        LocationEntity oldLocation = checkFindById(id);
        checkLocationName(update.getName(), id);
        checkLocationCapacity(update, oldLocation);
        LocationEntity updateEntity = setUpdateLocation(oldLocation, update);
        log.info("Запрос на обновление локации '{}' c id={} выполнен", updateEntity.getName(), updateEntity.getId());
        return locationConverter.convertToDomain(updateEntity);
    }

    @Transactional(readOnly = true)
    public Location findByIdLocation(Integer id) {
        log.info("Запрос на поиск локации у сервиса");
        log.info("Запрос на поиск локации у сервиса закончен");
        return locationConverter.convertToDomain(checkFindById(id));
    }

    @Transactional
    public void deleteLocation(Integer id) {
        log.info("Запрос на удаление локации по id={} у сервиса", id);
        checkFindById(id);
        locationRepository.deleteById(id);
        log.info("Локация по id={} удалена в сервисе", id);
    }

    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        log.info("Запрос на получение списка всех локаций у сервиса");
        List<Location> allLocations = locationRepository
                .findAll()
                .stream()
                .map(locationConverter::convertToDomain).toList();
        log.info("Найдено локаций:{}", allLocations.size());
        return allLocations;
    }

    private LocationEntity checkFindById(Integer id) {
        log.info("Запрос на получение локации по id={} у сервиса", id);
        return locationRepository.findById(id)
                .orElseThrow(
                        () -> {
                            log.warn("локация с id={} не найдена", id);
                            return new NotFoundLocationException(
                                    "Локации с id=%d не существует"
                                            .formatted(id));
                        });
    }

    private void checkLocationCapacity(Location locationDomain, LocationEntity updateLocation) {
        if (locationDomain.getCapacity() < updateLocation.getCapacity()) {
            log.warn("Произошла ошибка, вместимость у локации '{}' с id={} должна быть не меньше {}",
                    updateLocation.getName(), updateLocation.getId(), updateLocation.getCapacity());
            throw new LocationCapacityException("У локации '%s' вместительность %d, нельзя ставить меньшее число"
                    .formatted(updateLocation.getName(), updateLocation.getCapacity()));
        }

    }

    private void checkLocationName(String name, Integer excludeId) {
        boolean nameExists;
        if (excludeId == null) {
            nameExists = locationRepository.existsByName(name);
        } else {
            nameExists = locationRepository.existsByNameAndIdNot(name, excludeId);
        }
        if (nameExists) {
            throw new LocationTakenNameException("Локация с названием '%s' уже существует".formatted(name));
        }
    }

    private LocationEntity setUpdateLocation(LocationEntity oldLocation, Location updateLocation) {
        oldLocation.setName(updateLocation.getName());
        oldLocation.setAddress(updateLocation.getAddress());
        oldLocation.setCapacity(updateLocation.getCapacity());
        oldLocation.setDescription(updateLocation.getDescription());
        return locationRepository.save(oldLocation);
    }
}

