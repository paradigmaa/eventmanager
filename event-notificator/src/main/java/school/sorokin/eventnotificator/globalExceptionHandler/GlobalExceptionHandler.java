package school.sorokin.eventnotificator.globalExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.sorokin.eventnotificator.globalExceptionHandler.exceptionUtils.FieldError;
import school.sorokin.eventnotificator.globalExceptionHandler.exceptionUtils.ServerError;
import school.sorokin.eventnotificator.globalExceptionHandler.exceptionUtils.ValidationErrorResponse;


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
        String message = "Параметр имеет неверный тип";

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
