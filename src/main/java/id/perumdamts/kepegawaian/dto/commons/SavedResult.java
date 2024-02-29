package id.perumdamts.kepegawaian.dto.commons;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class SavedResult<T> extends ResultAbstract<T> {

    public SavedResult(SavedStatus<T> data) {
        switch (data.getStatus()) {
            case SUCCESS:
                this.message = "Data saved successfully";
                this.statusText = HttpStatus.CREATED;
                break;
            case FAILED:
                this.message = "Data failed to save";
                this.statusText = HttpStatus.BAD_REQUEST;
                break;
            case DUPLICATE:
                this.message = "Data already exists";
                this.statusText = HttpStatus.CONFLICT;
        }
    }
}
