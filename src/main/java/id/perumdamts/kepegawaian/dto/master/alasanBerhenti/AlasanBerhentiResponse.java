package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AlasanBerhentiResponse {
    private Long id;
    private String nama;
    private String notes;

    public static AlasanBerhentiResponse from(AlasanBerhenti entity) {
        AlasanBerhentiResponse response = new AlasanBerhentiResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        response.setNotes(entity.getNotes());
        return response;
    }
}
