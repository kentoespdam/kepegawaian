package id.perumdamts.kepegawaian.dto.commons;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeletedResult extends ResultAbstract<Object> {
    public DeletedResult(boolean data) {
        if (data) {
            this.setStatusText(HttpStatus.OK);
            this.setMessage("Data berhasil dihapus");
        } else {
            this.setStatusText(HttpStatus.BAD_REQUEST);
            this.setMessage("Data gagal dihapus");
        }
    }
}
