package id.perumdamts.kepegawaian.exceptions;

import org.springframework.http.HttpStatus;

public class CutiImportException extends ApiException {
    public CutiImportException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public CutiImportException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}
