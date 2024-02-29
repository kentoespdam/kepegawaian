package id.perumdamts.kepegawaian.dto.commons;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ErrorResult extends ResultAbstract<Object> {
    private List<String> errors = new ArrayList<>();

    private void addError(String message) {
        this.errors.add(message);
    }

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
        return ResponseEntity.status(result.statusText).body(result);
    }

    private static List<String> getErrors(Errors errors) {
        return errors.getFieldErrors()
                .stream()
                .map(error -> "field [" + error.getField() + "] : " + error.getDefaultMessage())
                .toList();
    }
}
