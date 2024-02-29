package id.perumdamts.kepegawaian.dto.commons;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class SingleResult<T> extends ResultAbstract<T> {
    public SingleResult(T data) {
        if (data != null) {
            this.data = data;
            this.statusText = HttpStatus.OK;
            this.message = "Data Found";
        } else {
            this.message = "Data not found!";
            this.statusText = HttpStatus.NOT_FOUND;
        }
    }
}
