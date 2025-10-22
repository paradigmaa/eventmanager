package school.sorokin.eventmanager.locations.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.sorokin.eventmanager.locations.dto.ServerErrorDto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServerErrorDto> handlerValidationException(MethodArgumentNotValidException e) {
        String detail = detailBindingResultHelper(e);
        log.warn("Ошибка валидации запроса: {}", detail);
        return createErrorMessageAndResponseEntity("Ошибка валидации запроса",
                detail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerErrorDto> handlerServerErrorException(Exception e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);
        return createErrorMessageAndResponseEntity("Ошибка сервера",
                e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundLocationException.class)
    public ResponseEntity<ServerErrorDto> handlerNotFoundLocationException(NotFoundLocationException e) {
        log.warn("Локация не найдена: {}", e.getMessage());
        return createErrorMessageAndResponseEntity("Сущность не найдена",
                e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LocationCapacityException.class)
    public ResponseEntity<ServerErrorDto> handlerLocationCapacityException(LocationCapacityException e) {
        log.warn("Ошибка вместимости локации: {}", e.getMessage());
        return createErrorMessageAndResponseEntity("Некорректная вместимость",
                e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LocationTakenNameException.class)
    public ResponseEntity<ServerErrorDto> handlerLocationTakenNameException(LocationTakenNameException e) {
        log.warn("Попытка использовать занятое имя локации: {}", e.getMessage());
        return createErrorMessageAndResponseEntity("Имя должно быть уникальным",
                e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private String detailBindingResultHelper(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(errors -> errors.getField() + ":" + errors.getDefaultMessage())
                .collect(Collectors.joining(","));
    }

    private ResponseEntity<ServerErrorDto> createErrorMessageAndResponseEntity(String message, String detail, HttpStatus httpStatus) {
        ServerErrorDto serverErrorDTO = new ServerErrorDto(
                message,
                detail,
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(httpStatus)
                .body(serverErrorDTO);
    }

}
