package school.sorokin.eventmanager.globalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import school.sorokin.eventmanager.globalExceptionHandler.exceptionUtils.FieldError;
import school.sorokin.eventmanager.globalExceptionHandler.exceptionUtils.ServerError;
import school.sorokin.eventmanager.globalExceptionHandler.exceptionUtils.ValidationErrorResponse;
import school.sorokin.eventmanager.events.exception.*;
import school.sorokin.eventmanager.locations.exception.LocationCapacityException;
import school.sorokin.eventmanager.locations.exception.LocationTakenNameException;
import school.sorokin.eventmanager.locations.exception.NotFoundLocationException;
import school.sorokin.eventmanager.users.exception.LoginTakenNameException;
import school.sorokin.eventmanager.users.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handlerValidationException(MethodArgumentNotValidException e) {
        List<FieldError> listError = detailBindingResultHelper(e);
        log.warn("Ошибка валидации тела запроса: {}", listError);

        ValidationErrorResponse response = new ValidationErrorResponse(
                "Ошибка валидации запроса мероприятия",
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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ServerError> handBadCredentialException(BadCredentialsException e) {
        log.warn("Попытка использовать авторизации неизвестного пользователя: {}", e.getMessage());
        return createServerErrorResponse("Пользователь не зарегистрирован в системе, " +
                "аутентификация невозможна", e.getMessage(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(LoginTakenNameException.class)
    public ResponseEntity<ServerError> handlerLoginTakeNameException(LoginTakenNameException e) {
        log.warn("Попытка использовать занятое имя пользователя: {}", e.getMessage());
        return createServerErrorResponse("Логин должен быть уникальным", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ServerError> handlerLoginTakeNameException(UserNotFoundException e) {
        log.warn("Такого пользователя не существует: {}", e.getMessage());
        return createServerErrorResponse("Такого пользователя не существует", e.getMessage(), HttpStatus.NOT_FOUND);
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
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ServerError> handlerEventNotFoundException(EventNotFoundException e) {
        log.warn("Мероприятие не найдено: {}", e.getMessage());
        return createServerErrorResponse("Мероприятие не найдено", e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EventCapacityExceededException.class)
    public ResponseEntity<ServerError> handlerEventCapacityExceededException(EventCapacityExceededException e) {
        log.warn("Ошибка вместимости локации для мероприятия: {}", e.getMessage());
        return createServerErrorResponse("Превышена вместимость локации", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventDateException.class)
    public ResponseEntity<ServerError> handlerEventDateException(EventDateException e) {
        log.warn("Ошибка даты мероприятия: {}", e.getMessage());
        return createServerErrorResponse("Некорректная дата мероприятия", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventFullException.class)
    public ResponseEntity<ServerError> handlerEventFullException(EventFullException e) {
        log.warn("Мероприятие переполнено: {}", e.getMessage());
        return createServerErrorResponse("Мероприятие переполнено", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventStatusException.class)
    public ResponseEntity<ServerError> handlerEventStatusException(EventStatusException e) {
        log.warn("Ошибка статуса мероприятия: {}", e.getMessage());
        return createServerErrorResponse("Некорректный статус мероприятия", e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EventDeleteException.class)
    public ResponseEntity<ServerError> handlerEventDeleteException(EventDeleteException e) {
        log.warn("Ошибка доступа к мероприятию: {}", e.getMessage());
        return createServerErrorResponse("Доступ запрещен", e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AlreadyEventNameExistException.class)
    public ResponseEntity<ServerError> handlerEventNameExistException(AlreadyEventNameExistException e) {
        log.warn("Попытка создать мероприятие с одинаковы  именем: {}", e.getMessage());
        return createServerErrorResponse("Такое мероприятие уже есть", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyRegisterException.class)
    public ResponseEntity<ServerError> handlerAlreadyRegisterException(AlreadyRegisterException e) {
        log.warn("Двойная регистрация на мероприятие: {}", e.getMessage());
        return createServerErrorResponse("Двойная регистрация", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserOwnerException.class)
    public ResponseEntity<ServerError> handlerUserOwnerException(UserOwnerException e) {
        log.warn("Попытка действий с данными пользователем, который не является админом или владельцем: {}", e.getMessage());
        return createServerErrorResponse("Попытка получить доступ", e.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(RegistrationNotFoundException.class)
    public ResponseEntity<ServerError> handlerRegistrationNotFoundException(RegistrationNotFoundException e) {
        log.warn("Регистрация не найдена: {}", e.getMessage());
        return createServerErrorResponse("Регистрация не найдена", e.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerError> handlerServerErrorException(Exception e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);
        return createServerErrorResponse("Ошибка сервера",
                "Произошла внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private List<FieldError> detailBindingResultHelper(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
    }

    private ResponseEntity<ServerError> createServerErrorResponse(String message, String detailMessage, HttpStatus httpStatus) {
        ServerError serverError = new ServerError(message, detailMessage, LocalDateTime.now());
        return ResponseEntity.status(httpStatus).body(serverError);
    }
}
