package id.perumdamts.kepegawaian.dto.commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ResultAbstract<T> {
    protected Integer status;
    protected HttpStatus statusText = HttpStatus.OK;
    protected String message;
    protected T data;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime timestamp=LocalDateTime.now();

    public Integer getStatus() {
        return statusText.value();
    }

}
