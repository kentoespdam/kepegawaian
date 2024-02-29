package id.perumdamts.kepegawaian.dto.master.statusPegawai;

import id.perumdamts.kepegawaian.entities.master.StatusPegawai;
import lombok.Data;

import java.util.List;

@Data
public class StatusPegawaiPostRequest {
    private String nama;

    public static StatusPegawai toEntity(StatusPegawaiPostRequest request) {
        StatusPegawai statusPegawai = new StatusPegawai();
        statusPegawai.setNama(request.getNama());
        return statusPegawai;
    }

    public static List<StatusPegawai> toEntities(List<StatusPegawaiPostRequest> requests) {
        return requests.stream().map(StatusPegawaiPostRequest::toEntity).toList();
    }
}
