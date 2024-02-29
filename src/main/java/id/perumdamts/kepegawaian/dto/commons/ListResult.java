package id.perumdamts.kepegawaian.dto.commons;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListResult<T> extends ResultAbstract<List<T>> {
    public ListResult(List<T> data) {
        if (!data.isEmpty()) {
            this.data = data;
            this.statusText = HttpStatus.OK;
            this.message = "Data found!";
        } else {
            this.statusText = HttpStatus.NOT_FOUND;
            this.message = "Data not found!";
        }
    }
}
