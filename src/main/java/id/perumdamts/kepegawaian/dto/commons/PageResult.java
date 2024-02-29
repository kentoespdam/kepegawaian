package id.perumdamts.kepegawaian.dto.commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PageResult<T> implements Serializable {
    private Integer status;
    private HttpStatus statusText = HttpStatus.OK;
    private T data;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public PageResult(T data) {
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
