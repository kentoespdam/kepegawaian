package id.perumdamts.kepegawaian.dto.master.statusPegawai;

import id.perumdamts.kepegawaian.entities.master.StatusPegawai;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatusPegawaiResponse {
    private Long id;
    private String nama;

    public static StatusPegawaiResponse from(StatusPegawai statusPegawai) {
        return new StatusPegawaiResponse(statusPegawai.getId(), statusPegawai.getNama());
    }
}
