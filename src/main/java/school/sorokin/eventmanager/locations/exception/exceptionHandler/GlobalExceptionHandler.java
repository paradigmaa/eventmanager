package school.sorokin.eventmanager.locations.exception.exceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import school.sorokin.eventmanager.locations.exception.*;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handlerValidationException(MethodArgumentNotValidException e) {
        List<FieldError> listError = detailBindingResultHelper(e);
        log.warn("Ошибка валидации тела запроса: {}", listError);

        ValidationErrorResponse response = new ValidationErrorResponse(
                "Ошибка валидации запроса",
                listError,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ServerError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = String.format("Параметр '%s' имеет неверный тип. Ожидается: %s",
                e.getName(), e.getRequiredType().getSimpleName());

        log.warn("Ошибка типа параметра: {}", message);
        return createServerErrorResponse("Неверный тип параметра", message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ServerError> handleMissingParams(MissingServletRequestParameterException e) {
        String message = String.format("Обязательный параметр '%s' отсутствует", e.getParameterName());

        log.warn("Отсутствует обязательный параметр: {}", message);
        return createServerErrorResponse("Отсутствует обязательный параметр", message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundLocationException.class)
    public ResponseEntity<ServerError> handlerNotFoundLocationException(NotFoundLocationException e) {
        log.warn("Локация не найдена: {}", e.getMessage());
        return createServerErrorResponse("Сущность не найдена", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LocationCapacityException.class)
    public ResponseEntity<ServerError> handlerLocationCapacityException(LocationCapacityException e) {
        log.warn("Ошибка вместимости локации: {}", e.getMessage());
        return createServerErrorResponse("Некорректная вместимость", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LocationTakenNameException.class)
    public ResponseEntity<ServerError> handlerLocationTakenNameException(LocationTakenNameException e) {
        log.warn("Попытка использовать занятое имя локации: {}", e.getMessage());
        return createServerErrorResponse("Имя должно быть уникальным", e.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerError> handlerServerErrorException(Exception e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);
        return createServerErrorResponse("Ошибка сервера",
                "Произошла внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private List<FieldError> detailBindingResultHelper(MethodArgumentNotValidException e){
        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
    }

    private ResponseEntity<ServerError> createServerErrorResponse(String message, String detailMessage, HttpStatus httpStatus) {
        ServerError serverError = new ServerError(message, detailMessage, LocalDateTime.now());
        return ResponseEntity.status(httpStatus).body(serverError);
    }
}
