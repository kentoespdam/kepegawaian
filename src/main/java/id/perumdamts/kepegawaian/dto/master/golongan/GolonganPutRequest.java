package id.perumdamts.kepegawaian.dto.master.golongan;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GolonganPutRequest extends GolonganPostRequest {
    public static Golongan toEntity(GolonganPutRequest request, Long id) {
        return new Golongan(id, request.getNama());
    }
}
