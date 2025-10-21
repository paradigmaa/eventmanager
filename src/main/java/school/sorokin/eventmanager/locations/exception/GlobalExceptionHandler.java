package school.sorokin.eventmanager.locations.exception;

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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServerErrorDto>handlerValidationException(MethodArgumentNotValidException e) {
    String detail = e.getBindingResult().getFieldErrors().stream()
            .map(errors -> errors.getField() + ":" + errors.getDefaultMessage())
            .collect(Collectors.joining("," ));
    ServerErrorDto serverErrorDto = new ServerErrorDto(
            "Ошибка валидации запроса",
            detail,
            LocalDateTime.now()
    );
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(serverErrorDto);
}


}
