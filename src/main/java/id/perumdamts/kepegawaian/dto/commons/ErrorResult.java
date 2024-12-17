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
    public static ResponseEntity<?> build(String message) {
        ErrorResult result = new ErrorResult();
        result.addError(message);
        result.setStatusText(HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(result.getStatusText()).body(result);
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

    public static <T> ResponseEntity<?> build(Set<ConstraintViolation<T>> validate) {
        ErrorResult result = new ErrorResult();
        result.setErrors(getErrors(validate));
        result.setStatusText(HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(result.getStatusText()).body(result);
    }

    private static <T> List<String> getErrors(Set<ConstraintViolation<T>> validate) {
        return validate.stream()
                .map(error -> "field [" + error.getPropertyPath() + "] : " + error.getMessage())
                .toList();
    }
}
