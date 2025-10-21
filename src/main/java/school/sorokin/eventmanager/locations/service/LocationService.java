package school.sorokin.eventmanager.locations.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sorokin.eventmanager.locations.domain.LocationDomain;
import school.sorokin.eventmanager.locations.entity.ConverterToEntity;
import school.sorokin.eventmanager.locations.entity.LocationEntity;
import school.sorokin.eventmanager.locations.exception.NotFoundLocationException;
import school.sorokin.eventmanager.locations.repository.LocationRepository;

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
        log.info("Запрос на создание локации");
        var newLocation = locationRepository
                .save(converter.convertToEntity(new LocationDomain(locationDomain)));
        log.info("Запрос на создание локации с id={} выполнен", newLocation.getId());
        log.debug("Данные о созданной локации: {}", newLocation);
        return converter.convertToDomain(newLocation);
    }

    @Transactional
    public LocationDomain updateLocation(Integer id, LocationDomain locationDomain) {
        log.info("Запрос на обновление локации");
        checkExistById(id);
        var updateLocation = locationRepository.updateLocation(id, locationDomain);
        log.info("Запрос на обновление локации {} c id {} выполнен",
                updateLocation.getName(),
                updateLocation.getId());
        log.debug("Данные об обновленной локации:{}", updateLocation);
        return converter.convertToDomain(locationRepository.findById(id).get());
    }

    public LocationDomain findByIdLocation(Integer id) {
        return converter.convertToDomain(checkFindByIdHelper(id));
    }
    @Transactional
    public void deleteLocation(Integer id) {
        log.info("Запрос на удаление локации по id={}",id);
        checkExistById(id);
        log.info("Локация по id={} удалена", id);
        locationRepository.deleteById(id);
    }

    private void checkExistById(Integer id) {
        log.info("Запрос на проверку локации по id");
        log.info("Проверка локации с id={}", id);
        if (!locationRepository.existsById(id)) {
            log.warn("локация с id={}не найдена", id);
            throw new NotFoundLocationException(
                    "Локации с id=%d не существует"
                            .formatted(id));
        }
        log.debug("локация с id={} существует в базе", id);
    }

    private LocationEntity checkFindByIdHelper(Integer id) {
        log.info("Запрос на получение локации по id={}", id);
        return locationRepository.findById(id)
                .orElseThrow(
                        () -> {
                            log.warn("локация с id={}не найдена", id);
                            return new NotFoundLocationException(
                                    "Локации с id=%d не существует"
                                            .formatted(id));
                        });
    }


}

