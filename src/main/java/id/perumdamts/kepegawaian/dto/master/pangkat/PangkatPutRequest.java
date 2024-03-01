package id.perumdamts.kepegawaian.dto.master.pangkat;

import id.perumdamts.kepegawaian.entities.master.Pangkat;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PangkatPutRequest extends PangkatPostRequest {
    public static Pangkat toEntity(PangkatPutRequest request, Long id){
        return new Pangkat(id, request.getNama());
    }
}
