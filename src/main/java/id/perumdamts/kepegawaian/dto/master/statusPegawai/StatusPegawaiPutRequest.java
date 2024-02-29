package id.perumdamts.kepegawaian.dto.master.statusPegawai;

import id.perumdamts.kepegawaian.entities.master.StatusPegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StatusPegawaiPutRequest extends StatusPegawaiPostRequest{
    public static StatusPegawai toEntity(StatusPegawaiPutRequest request, Long id){
        return new StatusPegawai(id, request.getNama());
    }
}
