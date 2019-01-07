package ru.mephi.prepod;

import com.google.common.collect.ImmutableMap;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class DatabaseExceptionHandler {

    private static final String ERROR = "error";

    public static ResponseEntity handle(DataIntegrityViolationException e) {
        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException inner = (ConstraintViolationException) e.getCause();
            if (inner.getCause() instanceof PSQLException) {
                PSQLException cause = (PSQLException) inner.getCause();
                return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR,
                        cause.getServerErrorMessage().getDetail()));
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ImmutableMap.of(ERROR, e));
    }
}
