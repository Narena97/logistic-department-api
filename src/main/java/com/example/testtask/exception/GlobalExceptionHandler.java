package com.example.testtask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Calendar;
import java.util.TimeZone;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ExceptionResponse entityNotFound(EntityNotFoundException exception) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("404 Not Found");
        response.setErrorMessage(exception.getMessage());
        response.setTimestamp(Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow")).getTime());

        return response;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public ExceptionResponse entityAlreadyExists(EntityExistsException exception) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("409 Conflict");
        response.setErrorMessage(exception.getMessage());
        response.setTimestamp(Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow")).getTime());

        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionResponse argumentIsNotValid(MethodArgumentNotValidException exception) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("400 Bad Request");
        response.setErrorMessage(exception.getBindingResult().getFieldErrors().get(0) != null ?
                exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage() :
                "");
        response.setTimestamp(Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow")).getTime());

        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ExceptionResponse argumentExists(ValidationException exception) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("400 Bad Request");
        response.setErrorMessage(exception.getMessage());
        response.setTimestamp(Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow")).getTime());

        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ExceptionResponse driversLicenseEnumIsNotValid(HttpMessageNotReadableException exception) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("400 Bad Request");
        response.setErrorMessage(exception.getMessage());
        response.setTimestamp(Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow")).getTime());

        return response;
    }
}
