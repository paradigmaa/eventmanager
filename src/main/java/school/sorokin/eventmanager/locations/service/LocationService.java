package school.sorokin.eventmanager.locations.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.locations.domain.LocationDomain;
import school.sorokin.eventmanager.locations.entity.ConverterToEntity;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.locations.exception.LocationCapacityException;
import school.sorokin.eventmanager.locations.exception.LocationTakenNameException;
import school.sorokin.eventmanager.locations.exception.NotFoundLocationException;
import school.sorokin.eventmanager.locations.repository.LocationRepository;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final ConverterToEntity converter;

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    public LocationService(LocationRepository locationRepository, ConverterToEntity converter) {
        this.locationRepository = locationRepository;
        this.converter = converter;
    }

    @Transactional
    public LocationDomain createLocation(LocationDomain locationDomain) {
        log.info("Запрос на создание локации у сервиса");
        checkLocationTakenName(locationDomain);
        var newLocation = locationRepository
                .save(converter.convertToEntity(new LocationDomain(locationDomain)));
        log.info("Запрос на создание локации '{}' с id={} у сервиса выполнен",
                newLocation.getName(),
                newLocation.getId());
        return converter.convertToDomain(newLocation);
    }

    @Transactional
    public LocationDomain updateLocation(Integer id, LocationDomain newLocation) {
        LocationEntity oldEntity = checkFindById(id);
        LocationDomain updateLocation = new LocationDomain(newLocation);
        checkLocationTakeNameUpdate(oldEntity, updateLocation);
        checkLocationCapacity(updateLocation, oldEntity);
        return converter.convertToDomain(checkFindById(id));
    }

    @Transactional(readOnly = true)
    public LocationDomain findByIdLocation(Integer id) {
        log.info("Запрос на поиск локации у сервиса");
        return converter.convertToDomain(checkFindById(id));
    }

    @Transactional
    public void deleteLocation(Integer id) {
        log.info("Запрос на удаление локации по id={} у сервиса", id);
        checkFindById(id);
        log.info("Локация по id={} удалена в сервисе", id);
        locationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LocationDomain> getAllLocations() {
        log.info("Запрос на получение списка всех локаций у сервиса");
        List<LocationDomain> allLocations = locationRepository
                .findAll()
                .stream()
                .map(converter::convertToDomain).toList();
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

    private void checkLocationCapacity(LocationDomain locationDomain, LocationEntity updateLocation) {
        if (locationDomain.getCapacity() < updateLocation.getCapacity()) {
            log.warn("Произошла ошибка, вместимость у локации '{}' с id={} должна быть не меньше {}",
                    updateLocation.getName(), updateLocation.getId(), updateLocation.getCapacity());
            throw new LocationCapacityException("У локации '%s' вместительность %d, нельзя ставить меньшее число"
                    .formatted(updateLocation.getName(), updateLocation.getCapacity()));
        }

    }

    private void checkLocationTakenName(LocationDomain locationDomain) {
        if (locationRepository.existsByName(locationDomain.getName())) {
            log.warn("Попытка создать дубликат локации c именем '{}'",
                    locationDomain.getName());
            throw new LocationTakenNameException("Локация с названием '%s' уже существует"
                    .formatted(locationDomain.getName()));
        }
    }

    private void checkLocationTakeNameUpdate(LocationEntity oldEntity, LocationDomain updateLocation) {
        if (updateLocation.getName().equals(oldEntity.getName())) {
            log.info("Запрос на обновление локации без изменения имени");
            locationRepository.updateLocation(oldEntity.getId(), updateLocation);
            log.info("Запрос на обновление локации '{}' c id={} без изменения имени выполнен",
                    updateLocation.getName(),
                    updateLocation.getId());
        } else {
            log.info("Запрос на обновление локации у сервиса с изменением имени");
            checkLocationTakenName(updateLocation);
            locationRepository.updateLocation(oldEntity.getId(), updateLocation);
            log.info("Запрос на обновление локации '{}' c id={} с изменением имени выполнен",
                    updateLocation.getName(),
                    oldEntity.getId());
        }

    }
}

