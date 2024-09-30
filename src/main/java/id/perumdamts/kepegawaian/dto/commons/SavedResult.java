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
                this.data = data.getData();
                this.statusText = HttpStatus.CREATED;
                break;
            case FAILED:
                this.addError(data.getData().toString());
//                this.message = data.getData().toString();
                this.statusText = HttpStatus.BAD_REQUEST;
                break;
            case DUPLICATE:
                this.addError(data.getData().toString());
//                this.message = data.getData().toString();
                this.statusText = HttpStatus.CONFLICT;
        }
    }
}
