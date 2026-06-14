package id.perumdamts.kepegawaian.dto.commons;

import jakarta.validation.ConstraintViolation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ErrorResult extends ResultAbstract<Object> {
    public static ResponseEntity<?> build(int statusCode, String message) {
        ErrorResult result = new ErrorResult();
        result.addError(message);
        result.setStatusText(HttpStatus.valueOf(statusCode));
        return ResponseEntity.status(statusCode).body(result);
    }

    public static ResponseEntity<?> build(String message) {
        return build(400, message);
    }

    public static ResponseEntity<?> build(Errors errors) {
        ErrorResult result = new ErrorResult();
        result.setErrors(getErrors(errors));
        result.setStatusText(HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(result.getStatusText()).body(result);
    }

    private static List<String> getErrors(Errors errors) {
        return errors.getFieldErrors()
                .stream()
                .map(error -> "field [" + error.getField() + "] : " + error.getDefaultMessage())
                .toList();
    }

    public static ResponseEntity<?> build(Set<? extends ConstraintViolation<?>> violations) {
        ErrorResult result = new ErrorResult();
        result.setErrors(getErrors(violations));
        result.setStatusText(HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(result.getStatusText()).body(result);
    }

    private static List<String> getErrors(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(error -> "field [" + error.getPropertyPath() + "] : " + error.getMessage())
                .toList();
    }
}
