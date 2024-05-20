package id.perumdamts.kepegawaian.dto.master.statusPegawai;

import id.perumdamts.kepegawaian.entities.master.StatusPegawai;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class StatusPegawaiPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;

    public static StatusPegawai toEntity(StatusPegawaiPostRequest request) {
        StatusPegawai statusPegawai = new StatusPegawai();
        statusPegawai.setNama(request.getNama());
        return statusPegawai;
    }

    public static StatusPegawai toEntity(StatusPegawaiPostRequest request, Long id) {
        return new StatusPegawai(id, request.getNama());
    }

    public static List<StatusPegawai> toEntities(List<StatusPegawaiPostRequest> requests) {
        return requests.stream().map(StatusPegawaiPostRequest::toEntity).toList();
    }
}
