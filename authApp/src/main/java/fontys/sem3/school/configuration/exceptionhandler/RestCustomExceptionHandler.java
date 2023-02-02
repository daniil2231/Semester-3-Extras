package fontys.sem3.school.configuration.exceptionhandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@ControllerAdvice
@Slf4j
public class RestCustomExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error.";
    private static final String ERROR_LOG_FIELD = "error";
    private static final String STATUS_LOG_FIELD = "status";

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Object> handleConstraintViolationException(final AccessDeniedException error) {
        log.error("Access Denied with status {} occurred {}", kv(STATUS_LOG_FIELD, HttpStatus.FORBIDDEN), error);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException error, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        log.error("MethodArgumentNotValidException with status {} occurred {}", kv(STATUS_LOG_FIELD, HttpStatus.BAD_REQUEST), error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(convertToResponseBody(error));
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException error) {
        log.error("ConstraintViolationException with status {} occurred {}", kv(STATUS_LOG_FIELD, HttpStatus.BAD_REQUEST), error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(convertToResponseBody(error));
    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    public ResponseEntity<Object> handleResponseStatusException(final ResponseStatusException error) {
        log.error("ResponseStatusException with status {} occurred {}", kv(STATUS_LOG_FIELD, error.getStatus()), error);
        return ResponseEntity.status(error.getStatus()).body(error.getReason());
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleUnknownRuntimeError(final RuntimeException error) {
        log.error("Internal server error occurred.", error);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR_MESSAGE);
    }

    private List<ValidationErrorDTO> convertToResponseBody(final MethodArgumentNotValidException error) {
        final BindingResult bindingResult = error.getBindingResult();
        final List<ValidationErrorDTO> result = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .forEach(validationError -> {
                        if (validationError instanceof final FieldError fieldError) {
                            result.add(new ValidationErrorDTO(fieldError.getField(), fieldError.getDefaultMessage()));
                        } else {
                            result.add(new ValidationErrorDTO(validationError.getObjectName(), validationError.getDefaultMessage()));
                        }
                    });
        } else {
            log.warn("MethodArgumentNotValidException without binding result errors {}", kv(ERROR_LOG_FIELD, error));
        }
        return result;
    }

    private List<ValidationErrorDTO> convertToResponseBody(final ConstraintViolationException error) {
        if (CollectionUtils.isEmpty(error.getConstraintViolations())) {
            log.warn("Empty constraints violation for error: {}", error.getMessage());
            return Collections.emptyList();
        }

        final List<ValidationErrorDTO> result = new ArrayList<>();
        error.getConstraintViolations().forEach(constraintViolation -> {
                    final String field = constraintViolation.getPropertyPath() != null ? constraintViolation.getPropertyPath().toString() : "unknown field";
                    result.add(new ValidationErrorDTO(field, constraintViolation.getMessage()));
                }
        );
        return result;
    }

    @RequiredArgsConstructor
    @Getter
    private static class ValidationErrorDTO {
        private final String field;
        private final String error;
    }
}