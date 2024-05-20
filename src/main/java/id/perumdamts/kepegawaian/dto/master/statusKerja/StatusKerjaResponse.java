package id.perumdamts.kepegawaian.dto.master.statusKerja;

import id.perumdamts.kepegawaian.entities.master.StatusKerja;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatusKerjaResponse {
    private Long id;
    private String nama;

    public static StatusKerjaResponse from(StatusKerja entity) {
        return new StatusKerjaResponse(entity.getId(), entity.getNama());
    }
}
