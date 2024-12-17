package id.perumdamts.kepegawaian.dto.commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResultAbstract<T> {
    protected Integer status;
    protected HttpStatus statusText = HttpStatus.OK;
    protected List<String> errors = new ArrayList<>();
    protected String message;
    protected T data;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime timestamp = LocalDateTime.now();

    public Integer getStatus() {
        return statusText.value();
    }
    public void addError(String message) {
        this.errors.add(message);
    }

}
