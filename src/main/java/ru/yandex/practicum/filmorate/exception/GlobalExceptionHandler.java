package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        log.error("Ошибка валидации: {}", ex.getMessage());
        return new ResponseEntity<>("{\"error\": \"" + ex.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Необработанная ошибка: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("{\"error\": \"Произошла внутренняя ошибка сервера\"}", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("Объект не найден: {}", ex.getMessage());
        return new ResponseEntity<>("{\"error\": \"" + ex.getMessage() + "\"}", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        log.error("Объект не найден: {}", ex.getMessage());
        return new ResponseEntity<>("{\"error\": \"" + ex.getMessage() + "\"}", HttpStatus.NOT_FOUND);
    }

}
